/*
 * Copyright 2026 Krzysztof Borowy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    val totalClicks: Long,
    val expiryDate: LocalDateTime?,
    val lastRedirect: LocalDateTime?,
) {

    @OptIn(ExperimentalTime::class)
    val expired: Boolean
        get() = expiryDate?.isInPast ?: false
}
