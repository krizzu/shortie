package com.kborowy.shortie.errors

sealed class AppError(message: String) : RuntimeException(message)

class UnexpectedAppError(message: String) : AppError(message)
