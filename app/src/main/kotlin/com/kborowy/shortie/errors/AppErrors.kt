package com.kborowy.shortie.errors

sealed class AppError(message: String) : RuntimeException(message)

/** Entered alias/hash is already taken and is violating unique constraint */
class AliasAlreadyExistsError(message: String = "Alias/hash already used") : AppError(message)

class ExpiryInPastError(message: String = "Expiry date cannot be in the past") : AppError(message)

class UnexpectedAppError(message: String) : AppError(message)

/** Token validation errors */
sealed class TokenValidationError(message: String) : AppError(message)

class TokenExpiredError(message: String = "token has expired") : TokenValidationError(message)

class TokenVerificationError(message: String) : TokenValidationError(message)
