package com.kborowy.shortie.errors

import io.ktor.http.HttpStatusCode

/** Error base for all errors thrown within the app */
sealed class AppHttpError(val statusCode: HttpStatusCode, message: String) :
    RuntimeException(message)

class UnauthorizedHttpError(message: String) : AppHttpError(HttpStatusCode.Unauthorized, message)

class NotFoundHttpErrors(message: String) : AppHttpError(HttpStatusCode.NotFound, message)
