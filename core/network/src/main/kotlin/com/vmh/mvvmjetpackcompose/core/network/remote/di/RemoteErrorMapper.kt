package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapCatching
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.vmh.mvvmjetpackcompose.core.common.util.nonFatalOrThrow
import com.vmh.mvvmjetpackcompose.core.model.error.AppError.ApiException
import com.vmh.mvvmjetpackcompose.core.model.error.AppError.ApiException.ServerException
import com.vmh.mvvmjetpackcompose.core.model.error.AppError.ApiException.ServerException.ErrorDetails
import com.vmh.mvvmjetpackcompose.core.model.error.AppError.ApiException.ServerException.StatusCode
import com.vmh.mvvmjetpackcompose.core.network.remote.response.ErrorResponse
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.collections.find
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.use
import kotlin.jvm.Throws
import kotlin.takeUnless
import kotlin.to
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import retrofit2.HttpException

interface RemoteErrorMapper : (Throwable) -> ApiException

@Suppress("unused") // Public API
@OptIn(ExperimentalContracts::class)
internal suspend inline fun <T> (suspend () -> T).catchingApiException(
  remoteErrorMapper: RemoteErrorMapper,
): Result<T, ApiException> {
  contract {
    callsInPlace(this@catchingApiException, InvocationKind.AT_MOST_ONCE)
  }

  return runSuspendCatching { this() }.mapError(remoteErrorMapper)
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> catchingApiException(
  remoteErrorMapper: RemoteErrorMapper,
  block: () -> T,
): Result<T, ApiException> {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }

  return runSuspendCatching(block).mapError(remoteErrorMapper)
}

@Suppress(
  "unused", // Public API
  "NOTHING_TO_INLINE",
)
internal inline fun <T> Flow<T>.catchingApiException(
  remoteErrorMapper: RemoteErrorMapper,
): Flow<Result<T, ApiException>> = map<T, Result<T, ApiException>> { Ok(it) }
  .catch { emit(Err(remoteErrorMapper(it))) }

// ---------------------------------------------- RemoteErrorMapperImpl ----------------------------------------------

internal class RemoteErrorMapperImpl @Inject constructor(
  private val errorResponseJsonAdapter: JsonAdapter<ErrorResponse>,
  private val mapJsonAdapter: JsonAdapter<Map<String, Any?>>,
) : RemoteErrorMapper {
  override fun invoke(t: Throwable): ApiException = runCatching {
    when (val throwable = t.nonFatalOrThrow()) {
      is ApiException -> throwable
      is JsonDataException -> ApiException.InvalidDataException(throwable)
      is IOException -> throwable.toApiException()
      is HttpException -> throwable.toApiException()
      else -> ApiException.UnknownException(throwable)
    }
  }.getOrElse { ApiException.UnknownException(it.nonFatalOrThrow()) }

  private fun HttpException.toApiException(): ApiException {
    val errorBodyString = errorBodyAsString()
    val httpExceptionMessage = this.message()
    val statusCode = this.code()

    val (errorCode, message) = runCatching { errorResponseJsonAdapter.fromJson(errorBodyString)!! }
      .mapCatching { errorResponse ->
        val detail = errorResponse.error!!

        val errorCode: String? = detail.code ?: runCatching { mapJsonAdapter.fromJson(errorBodyString)!! }
          .mapCatching { it["code"] as String }
          .get()
        val message: String = detail.message ?: httpExceptionMessage

        errorCode to message
      }
      .fold(
        success = { it },
        failure = { null to httpExceptionMessage },
      )

    return ServerException(
      cause = this,
      details = ErrorDetails(
        message = message,
        statusCode = checkNotNull(StatusCode.entries.find { it.code == statusCode }) {
          "Cannot find StatusCode for HTTP status code: $statusCode"
        },
      ),
    )
  }
}

private fun IOException.toApiException(): ApiException = when (this) {
  is UnknownHostException, is SocketException -> ApiException.NetworkException(this)
  is SocketTimeoutException -> ApiException.TimeoutException(this)
  is JsonEncodingException -> ApiException.InvalidDataException(this)
  else -> ApiException.UnknownException(this)
}

@Throws(Exception::class)
private fun HttpException.errorBodyAsString(): String = response()!!
  .takeUnless { it.isSuccessful }!!
  .errorBody()!!
  .use(ResponseBody::string)
