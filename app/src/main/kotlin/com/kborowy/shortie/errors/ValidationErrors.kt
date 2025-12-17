package com.kborowy.shortie.errors

/** Alias already exists */
class AliasAlreadyExistsError(message: String = "Alias/hash already used") :
    BadRequestError(message)

/** Tried to set expiry date in past */
class ExpiryInPastError(message: String = "Expiry date cannot be in the past") :
    BadRequestError(message)
