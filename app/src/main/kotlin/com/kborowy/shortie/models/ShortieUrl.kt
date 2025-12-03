package com.kborowy.shortie.models

import kotlinx.datetime.LocalDateTime
import com.kborowy.shortie.utils.UrlValidator

@JvmInline
value class ShortCode(val code: String) {
    init {
        require(code.isNotEmpty()) { "short code cannot be empty" }
    }
}

@JvmInline
value class OriginalUrl(val url: String) {
    init {
        require(UrlValidator.validate(url)) { "$url is not a valid url" }
    }
}

data class ShortieUrl(
    val shortCode: ShortCode,
    val originalUrl: OriginalUrl,
    val password: String? = null,
    val expiry: LocalDateTime? = null,
) {

    val protected: Boolean
        get() = password != null
}
