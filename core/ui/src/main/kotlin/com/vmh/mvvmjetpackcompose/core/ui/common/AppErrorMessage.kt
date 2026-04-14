package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.error.AppError.ApiException.ServerException.StatusCode
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR

@Immutable
sealed interface AppErrorMessage {
  val title: String
  val message: String?
  val positiveButton: String?

  data class Inline(override val title: String, override val message: String?, override val positiveButton: String?) :
    AppErrorMessage {
    fun toDialog(newPositiveButton: String?) = Dialog(
      title = title,
      message = message,
      positiveButton = newPositiveButton,
    )
  }

  data class Dialog(override val title: String, override val message: String?, override val positiveButton: String?) :
    AppErrorMessage

  companion object {
    //region Inline
    @Composable
    @ReadOnlyComposable
    fun internalServerError(): Inline = Inline(
      title = stringResource(CoreResourceR.string.app_error_internal_server_title),
      message = stringResource(CoreResourceR.string.app_error_please_try_again),
      positiveButton = stringResource(CoreResourceR.string.app_error_inline_positive_button),
    )

    @Composable
    @ReadOnlyComposable
    fun badRequestError(): Inline = Inline(
      title = stringResource(CoreResourceR.string.app_error_some_thing_went_wrong),
      message = stringResource(CoreResourceR.string.app_error_please_try_again),
      positiveButton = stringResource(CoreResourceR.string.app_error_inline_positive_button),
    )

    @Composable
    @ReadOnlyComposable
    fun notFoundError(): Inline = Inline(
      title = stringResource(CoreResourceR.string.app_error_some_thing_went_wrong),
      message = stringResource(CoreResourceR.string.app_error_please_try_again),
      positiveButton = stringResource(CoreResourceR.string.app_error_inline_positive_button),
    )

    @Composable
    @ReadOnlyComposable
    fun networkConnectionError(): Inline = Inline(
      title = stringResource(CoreResourceR.string.app_error_no_internet_connection_title),
      message = stringResource(CoreResourceR.string.app_error_no_internet_connection_message),
      positiveButton = stringResource(CoreResourceR.string.app_error_inline_positive_button),
    )
    //endregion

    //region Dialog

    @Composable
    @ReadOnlyComposable
    fun internalServerErrorDialog(): Dialog = internalServerError()
      .toDialog(newPositiveButton = stringResource(CoreResourceR.string.app_error_dialog_positive_button))

    @Composable
    @ReadOnlyComposable
    fun badRequestErrorDialog(): Dialog = badRequestError()
      .toDialog(newPositiveButton = stringResource(CoreResourceR.string.app_error_dialog_positive_button))

    @Composable
    @ReadOnlyComposable
    fun notFoundErrorDialog(): Dialog = notFoundError()
      .toDialog(newPositiveButton = stringResource(CoreResourceR.string.app_error_dialog_positive_button))

    @Composable
    @ReadOnlyComposable
    fun networkConnectionErrorDialog(): Dialog = networkConnectionError()
      .toDialog(newPositiveButton = stringResource(CoreResourceR.string.app_error_dialog_positive_button))
    //endregion
  }
}

interface GetAppErrorMessage {
  @Composable
  @ReadOnlyComposable
  fun from(appError: AppError): AppErrorMessage?
}

fun GetAppErrorMessage.orElse(alternative: GetAppErrorMessage): GetAppErrorMessage {
  val self = this
  return object : GetAppErrorMessage {
    @Composable
    @ReadOnlyComposable
    override fun from(appError: AppError): AppErrorMessage? = self.from(appError) ?: alternative.from(appError)
  }
}

@Stable
val DefaultGetAppErrorMessageForInline: GetAppErrorMessage = object : GetAppErrorMessage {
  @Composable
  @ReadOnlyComposable
  override fun from(appError: AppError): AppErrorMessage.Inline? = when (appError) {
    is AppError.ApiException.ServerException -> when (appError.details.statusCode) {
      StatusCode.NotFound ->
        AppErrorMessage.notFoundError()

      StatusCode.PaymentRequired,
      StatusCode.Forbidden,
      StatusCode.MethodNotAllowed,
      StatusCode.NotAcceptable,
      StatusCode.ProxyAuthenticationRequired,
      StatusCode.RequestTimeout,
      StatusCode.Conflict,
      StatusCode.Gone,
      StatusCode.LengthRequired,
      StatusCode.PreconditionFailed,
      StatusCode.PayloadTooLarge,
      StatusCode.UriTooLong,
      StatusCode.UnsupportedMediaType,
      StatusCode.RangeNotSatisfiable,
      StatusCode.ExpectationFailed,
      StatusCode.IAmATeapot,
      StatusCode.MisdirectedRequest,
      StatusCode.UnprocessableEntity,
      StatusCode.Locked,
      StatusCode.FailedDependency,
      StatusCode.UpgradeRequired,
      StatusCode.PreconditionRequired,
      StatusCode.TooManyRequests,
      StatusCode.RequestHeaderFieldsTooLarge,
      StatusCode.UnavailableForLegalReasons,
      StatusCode.BadRequest,
      ->
        AppErrorMessage.badRequestError()

      StatusCode.InternalServerError,
      StatusCode.NotImplemented,
      StatusCode.BadGateway,
      StatusCode.ServiceUnavailable,
      StatusCode.GatewayTimeout,
      StatusCode.HttpVersionNotSupported,
      StatusCode.VariantAlsoNegotiates,
      StatusCode.InsufficientStorage,
      StatusCode.LoopDetected,
      StatusCode.NotExtended,
      StatusCode.NetworkAuthenticationRequired,
      ->
        AppErrorMessage.internalServerError()

      StatusCode.Unauthorized -> null

      else -> AppErrorMessage.networkConnectionError()
    }

    is AppError.ApiException.TimeoutException ->
      AppErrorMessage.badRequestError()

    else -> AppErrorMessage.networkConnectionError()
  }
}

@Stable
val DefaultGetAppErrorMessageForDialog: GetAppErrorMessage = object : GetAppErrorMessage {
  @Composable
  @ReadOnlyComposable
  override fun from(appError: AppError): AppErrorMessage.Dialog? = when (appError) {
    is AppError.ApiException.ServerException -> when (appError.details.statusCode) {
      StatusCode.NotFound ->
        AppErrorMessage.notFoundErrorDialog()

      StatusCode.PaymentRequired,
      StatusCode.Forbidden,
      StatusCode.MethodNotAllowed,
      StatusCode.NotAcceptable,
      StatusCode.ProxyAuthenticationRequired,
      StatusCode.RequestTimeout,
      StatusCode.Conflict,
      StatusCode.Gone,
      StatusCode.LengthRequired,
      StatusCode.PreconditionFailed,
      StatusCode.PayloadTooLarge,
      StatusCode.UriTooLong,
      StatusCode.UnsupportedMediaType,
      StatusCode.RangeNotSatisfiable,
      StatusCode.ExpectationFailed,
      StatusCode.IAmATeapot,
      StatusCode.MisdirectedRequest,
      StatusCode.UnprocessableEntity,
      StatusCode.Locked,
      StatusCode.FailedDependency,
      StatusCode.UpgradeRequired,
      StatusCode.PreconditionRequired,
      StatusCode.TooManyRequests,
      StatusCode.RequestHeaderFieldsTooLarge,
      StatusCode.UnavailableForLegalReasons,
      StatusCode.BadRequest,
      ->
        AppErrorMessage.badRequestErrorDialog()

      StatusCode.InternalServerError,
      StatusCode.NotImplemented,
      StatusCode.BadGateway,
      StatusCode.ServiceUnavailable,
      StatusCode.GatewayTimeout,
      StatusCode.HttpVersionNotSupported,
      StatusCode.VariantAlsoNegotiates,
      StatusCode.InsufficientStorage,
      StatusCode.LoopDetected,
      StatusCode.NotExtended,
      StatusCode.NetworkAuthenticationRequired,
      ->
        AppErrorMessage.internalServerErrorDialog()

      StatusCode.Unauthorized -> null

      else -> AppErrorMessage.networkConnectionErrorDialog()
    }

    is AppError.ApiException.TimeoutException ->
      AppErrorMessage.badRequestErrorDialog()

    is AppError.ApiException.NetworkException,
    is AppError.AuthException.NetworkException,
    ->
      AppErrorMessage.networkConnectionErrorDialog()

    else -> AppErrorMessage.badRequestErrorDialog()
  }
}
