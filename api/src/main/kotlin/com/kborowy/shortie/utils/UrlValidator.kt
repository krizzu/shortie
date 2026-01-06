package com.kborowy.shortie.utils

import org.apache.commons.validator.routines.UrlValidator as Validator

private val VALID_SCHEMES =
    listOf("http", "https", "ftp", "ftps", "sftp", "file", "data", "mailto", "tel", "ws", "wss")

object UrlValidator {
    fun validate(url: String): Boolean {
        val validator = Validator(VALID_SCHEMES.toTypedArray(), (Validator.ALLOW_LOCAL_URLS))
        return validator.isValid(url)
    }
}
