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

import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.extensions.toLocalDateTimeUTC
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortiePageCursor
import com.kborowy.shortie.models.ShortieUrl
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

interface UrlsRepository {
    suspend fun insert(
        url: OriginalUrl,
        code: ShortCode,
        expiry: LocalDateTime? = null,
        hash: String? = null,
    ): ShortieUrl

    suspend fun remove(shortCode: ShortCode): Boolean

    suspend fun remove(shortCodes: List<ShortCode>): Int

    suspend fun get(shortCode: ShortCode): ShortieUrl?

    suspend fun getHashForCode(shortCode: ShortCode): String?

    suspend fun getPage(limit: Int = 25, nextCursor: ShortiePageCursor? = null): ShortieUrlPaginated
}

fun UrlsRepository(db: Database): UrlsRepository = RealUrlsRepository(db)

// Implementation
private class RealUrlsRepository(private val db: Database) : UrlsRepository {

    override suspend fun insert(
        url: OriginalUrl,
        code: ShortCode,
        expiry: LocalDateTime?,
        hash: String?,
    ): ShortieUrl =
        transaction(db) {
            val addedId =
                UrlsTable.insertAndGetId {
                    it[shortCode] = code.value
                    it[originalUrl] = url.value
                    it[passwordHash] = hash
                    it[expiryDate] = expiry
                }

            UrlsTable.selectAll().where { UrlsTable.id eq addedId }.single().toShortieUrl()
        }

    override suspend fun remove(shortCode: ShortCode): Boolean =
        transaction(db) {
            val deleted = UrlsTable.deleteWhere { UrlsTable.shortCode eq shortCode.value }
            deleted > 0
        }

    override suspend fun remove(shortCodes: List<ShortCode>): Int {
        val list = shortCodes.map { it.value }.distinct()
        return transaction(db) { UrlsTable.deleteWhere { UrlsTable.shortCode inList list } }
    }

    override suspend fun get(shortCode: ShortCode): ShortieUrl? =
        transaction(db) {
            UrlsTable.selectAll()
                .where { UrlsTable.shortCode eq shortCode.value }
                .singleOrNull()
                ?.toShortieUrl()
        }

    override suspend fun getHashForCode(shortCode: ShortCode): String? =
        transaction(db) {
            UrlsTable.select(UrlsTable.passwordHash)
                .where { UrlsTable.shortCode eq shortCode.value }
                .singleOrNull()
                ?.getOrNull(UrlsTable.passwordHash)
        }

    override suspend fun getPage(limit: Int, nextCursor: ShortiePageCursor?): ShortieUrlPaginated {

        return transaction(db) {
            val rows =
                UrlsTable.selectAll()
                    .apply {
                        if (nextCursor != null) {
                            where {
                                (UrlsTable.createdAt less
                                    nextCursor.createdAt.toLocalDateTimeUTC) or
                                    ((UrlsTable.createdAt eq
                                        nextCursor.createdAt.toLocalDateTimeUTC) and
                                        (UrlsTable.id less nextCursor.id))
                            }
                        }
                    }
                    .orderBy(UrlsTable.createdAt to SortOrder.DESC, UrlsTable.id to SortOrder.DESC)
                    .limit(limit + 1) // neat way to detect if there is more
                    .toList()

            val items = rows.take(limit)
            val hasMore = rows.size > limit
            val next: ShortiePageCursor? =
                items.lastOrNull()?.let {
                    if (hasMore) {
                        ShortiePageCursor(
                            it[UrlsTable.id].value,
                            createdAt = it[UrlsTable.createdAt].asInstantUTC,
                        )
                    } else null
                }

            val shortUrls = items.map(ResultRow::toShortieUrl)
            ShortieUrlPaginated(shortUrls, hasNext = hasMore, next)
        }
    }
}

private fun ResultRow.toShortieUrl(): ShortieUrl {
    return ShortieUrl(
        shortCode = ShortCode(this[UrlsTable.shortCode]),
        originalUrl = OriginalUrl(this[UrlsTable.originalUrl]),
        protected = this[UrlsTable.passwordHash] != null,
        expiryDate = this[UrlsTable.expiryDate],
    )
}
