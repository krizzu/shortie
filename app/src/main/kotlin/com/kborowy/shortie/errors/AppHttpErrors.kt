package com.kborowy.shortie.errors

import io.ktor.http.HttpStatusCode

/** Error base for all http errors thrown within the app */
sealed class AppHttpError(val statusCode: HttpStatusCode, message: String) :
    RuntimeException(message)

class UnauthorizedHttpError(message: String = "unauthorized access") :
    AppHttpError(HttpStatusCode.Unauthorized, message)

class NotFoundHttpError(message: String = "resource not found") :
    AppHttpError(HttpStatusCode.NotFound, message)

class GoneHttpError(message: String = "gone") : AppHttpError(HttpStatusCode.Gone, message)

class BadRequestError(message: String = "bad request") :
    AppHttpError(HttpStatusCode.BadRequest, message)
