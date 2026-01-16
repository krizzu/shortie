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
package com.kborowy.shortie.data.urls

import com.kborowy.shortie.extensions.now
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

const val MAX_SHORT_CODE_LENGTH = 30
private const val MAX_LONG_URL_LENGTH = 2048
private const val MAX_PASSWORD_LENGTH = 255

object UrlsTable : LongIdTable(name = "urls") {
    val shortCode = varchar("short_code", MAX_SHORT_CODE_LENGTH).uniqueIndex()
    val originalUrl = varchar("original_url", MAX_LONG_URL_LENGTH)
    val passwordHash = varchar("password_hash", MAX_PASSWORD_LENGTH).nullable().default(null)
    val expiryDate = datetime("expiry_date").nullable().default(null)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now }

    // used for analytics
    val totalClicks = long("total_clicks").default(0)
    val lastRedirectAt = datetime("last_redirect").nullable()

    init {
        index(
            customIndexName = "idx_urls_created_at_id_desc",
            isUnique = false,
            createdAt,
            id,
        )
    }
}
