package com.kborowy.shortie.data.urls

import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.extensions.toLocalDateTimeUTC
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortiePageCursorDTO
import com.kborowy.shortie.models.ShortieUrl
import com.kborowy.shortie.utils.ShortCodeGenerator
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
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

    suspend fun get(shortCode: ShortCode): ShortieUrl?

    suspend fun getHashForCode(shortCode: ShortCode): String?

    suspend fun getPaginated(
        limit: Int = 25,
        nextCursor: ShortiePageCursorDTO? = null,
    ): ShortieUrlPaginated
}

fun UrlsRepository(db: Database, coder: ShortCodeGenerator): UrlsRepository =
    RealUrlsRepository(db, coder)

// Implementation
private class RealUrlsRepository(private val db: Database, private val coder: ShortCodeGenerator) :
    UrlsRepository {

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

    override suspend fun getPaginated(
        limit: Int,
        nextCursor: ShortiePageCursorDTO?,
    ): ShortieUrlPaginated {

        return transaction(db) {
            val rows =
                UrlsTable.selectAll()
                    .apply {
                        if (nextCursor != null) {
                            val id = coder.decodeShortCode(ShortCode(nextCursor.shortCode))
                            requireNotNull(id)
                            where {
                                (UrlsTable.createdAt less
                                    nextCursor.createdAt.toLocalDateTimeUTC) or
                                    ((UrlsTable.createdAt eq
                                        nextCursor.createdAt.toLocalDateTimeUTC) and
                                        (UrlsTable.id less id))
                            }
                        }
                    }
                    .orderBy(UrlsTable.createdAt to SortOrder.DESC, UrlsTable.id to SortOrder.DESC)
                    .limit(limit + 1) // neat way to detect if there is more
                    .toList()

            val items = rows.take(limit)
            val hasMore = rows.size > limit
            val next: ShortiePageCursorDTO? =
                items.lastOrNull()?.let {
                    if (hasMore) {
                        ShortiePageCursorDTO(
                            coder.generateShortCode(it[UrlsTable.id].value).value,
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
