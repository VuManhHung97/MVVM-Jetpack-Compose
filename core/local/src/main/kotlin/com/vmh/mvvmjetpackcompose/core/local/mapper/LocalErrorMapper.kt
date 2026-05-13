package com.vmh.mvvmjetpackcompose.core.local.mapper

import android.database.SQLException
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.vmh.mvvmjetpackcompose.core.common.util.nonFatalOrThrow
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import java.io.IOException
import javax.inject.Inject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

interface LocalErrorMapper : (Throwable) -> AppError.LocalStorageException

@Suppress("unused")
@OptIn(ExperimentalContracts::class)
internal suspend inline fun <T> (suspend () -> T).catchingLocalStorageException(
  localErrorMapper: LocalErrorMapper,
): Result<T, AppError.LocalStorageException> {
  contract {
    callsInPlace(this@catchingLocalStorageException, InvocationKind.AT_MOST_ONCE)
  }
  return runSuspendCatching { this() }.mapError(localErrorMapper)
}

@OptIn(ExperimentalContracts::class)
inline fun <T> catchingLocalStorageException(
  localErrorMapper: LocalErrorMapper,
  block: () -> T,
): Result<T, AppError.LocalStorageException> {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }
  return runSuspendCatching(block).mapError(localErrorMapper)
}

fun <T> Flow<T>.catchingLocalStorageException(
  localErrorMapper: LocalErrorMapper,
): Flow<Result<T, AppError.LocalStorageException>> = map<T, Result<T, AppError.LocalStorageException>> {
  Ok(
    it,
  )
}
  .catch { emit(Err(localErrorMapper(it))) }

internal class LocalErrorMapperImpl @Inject constructor() : LocalErrorMapper {
  override fun invoke(t: Throwable) = runCatching {
    when (val throwable = t.nonFatalOrThrow()) {
      is AppError.LocalStorageException -> throwable
      is IOException -> throwable.toLocalStorageException()
      is SQLException -> AppError.LocalStorageException.DatabaseException(throwable)
      else -> AppError.LocalStorageException.UnknownException(throwable)
    }
  }.getOrElse { it.nonFatalOrThrow().toLocalStorageException() }
}

private fun IOException.toLocalStorageException() = when (this) {
  is FileSystemException -> AppError.LocalStorageException.FileException(this)
  else -> AppError.LocalStorageException.UnknownException(this)
}

private fun Throwable.toLocalStorageException() = AppError.LocalStorageException.UnknownException(nonFatalOrThrow())
