@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.core.model.error

import java.io.Serializable

sealed class AppError :
  RuntimeException,
  Serializable {
  constructor() : super()
  constructor(message: String?) : super(message)
  constructor(message: String?, cause: Throwable?) : super(message, cause)
  constructor(cause: Throwable?) : super(cause)

  sealed class ApiException(cause: Throwable?) : AppError(cause) {
    class NetworkException(cause: Throwable?) : ApiException(cause)

    class InvalidDataException(cause: Throwable?) : ApiException(cause)

    class ServerException(cause: Throwable?, val details: ErrorDetails) : ApiException(cause) {
      data class ErrorDetails(val message: String, val statusCode: StatusCode)

      enum class StatusCode(val code: Int) {
        Continue(100),
        SwitchingProtocols(101),
        Processing(102),

        OK(200),
        Created(201),
        Accepted(202),
        NonAuthoritativeInformation(203),
        NoContent(204),
        ResetContent(205),
        PartialContent(206),
        MultiStatus(207),
        AlreadyReported(208),
        IMUsed(226),

        MultipleChoices(300),
        MovedPermanently(301),
        Found(302),
        SeeOther(303),
        NotModified(304),
        UseProxy(305),
        TemporaryRedirect(307),
        PermanentRedirect(308),

        BadRequest(400),
        Unauthorized(401),
        PaymentRequired(402),
        Forbidden(403),
        NotFound(404),
        MethodNotAllowed(405),
        NotAcceptable(406),
        ProxyAuthenticationRequired(407),
        RequestTimeout(408),
        Conflict(409),
        Gone(410),
        LengthRequired(411),
        PreconditionFailed(412),
        PayloadTooLarge(413),
        UriTooLong(414),
        UnsupportedMediaType(415),
        RangeNotSatisfiable(416),
        ExpectationFailed(417),
        IAmATeapot(418),
        MisdirectedRequest(421),
        UnprocessableEntity(422),
        Locked(423),
        FailedDependency(424),
        UpgradeRequired(426),
        PreconditionRequired(428),
        TooManyRequests(429),
        RequestHeaderFieldsTooLarge(431),
        UnavailableForLegalReasons(451),

        InternalServerError(500),
        NotImplemented(501),
        BadGateway(502),
        ServiceUnavailable(503),
        GatewayTimeout(504),
        HttpVersionNotSupported(505),
        VariantAlsoNegotiates(506),
        InsufficientStorage(507),
        LoopDetected(508),
        NotExtended(510),
        NetworkAuthenticationRequired(511),
      }
    }

    class TimeoutException(cause: Throwable?) : ApiException(cause)

    class UnknownException(cause: Throwable?) : ApiException(cause)
  }

  sealed class AuthException(cause: Throwable?) : AppError(cause) {
    class InvalidCredentialsException(cause: Throwable?) : AuthException(cause)

    class UserCollisionException(cause: Throwable?) : AuthException(cause)

    class NetworkException(cause: Throwable?) : AuthException(cause)

    class CanceledException(cause: Throwable?) : AuthException(cause)

    class UnknownException(cause: Throwable?) : AuthException(cause)
  }

  sealed class LocalStorageException(cause: Throwable?) : AppError(cause) {
    class FileException(cause: Throwable?) : LocalStorageException(cause)

    class DatabaseException(cause: Throwable?) : LocalStorageException(cause)

    class UnknownException(cause: Throwable?) : LocalStorageException(cause)
  }

  class UnknownException(cause: Throwable?) : AppError(cause)

  companion object {
    private const val serialVersionUID: Long = 1L
  }
}
