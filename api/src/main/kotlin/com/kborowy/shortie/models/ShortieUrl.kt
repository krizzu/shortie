package com.kborowy.shortie.models

import com.kborowy.shortie.extensions.isInPast
import com.kborowy.shortie.utils.UrlValidator
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime

@JvmInline
value class ShortCode(val value: String) {
    init {
        require(value.isNotEmpty()) { "short code cannot be empty" }
    }
}

@JvmInline
value class OriginalUrl(val value: String) {
    init {
        require(UrlValidator.validate(value)) { "$value is not a valid url" }
    }
}

data class ShortieUrl(
    val shortCode: ShortCode,
    val originalUrl: OriginalUrl,
    val protected: Boolean,
    val expiryDate: LocalDateTime? = null,
) {

    @OptIn(ExperimentalTime::class)
    val expired: Boolean
        get() = expiryDate?.isInPast ?: false
}
