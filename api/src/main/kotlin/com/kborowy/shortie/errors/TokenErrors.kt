package com.kborowy.shortie.errors

/** Token validation errors */
sealed class TokenError(message: String) : UnauthorizedHttpError(message)

class TokenExpiredError(message: String = "token has expired") : TokenError(message)

class TokenVerificationError(message: String) : TokenError(message)
