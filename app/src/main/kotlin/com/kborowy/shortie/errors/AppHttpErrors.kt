package com.kborowy.shortie.errors

import io.ktor.http.HttpStatusCode

/** Error base for all http errors thrown within the app */
sealed class AppHttpError(val statusCode: HttpStatusCode, message: String) :
    RuntimeException(message)

open class UnauthorizedHttpError(message: String = "unauthorized access") :
    AppHttpError(HttpStatusCode.Unauthorized, message)

class NotFoundHttpError(message: String = "resource not found") :
    AppHttpError(HttpStatusCode.NotFound, message)

class GoneHttpError(message: String = "gone") : AppHttpError(HttpStatusCode.Gone, message)

open class BadRequestError(message: String = "bad request") :
    AppHttpError(HttpStatusCode.BadRequest, message)
