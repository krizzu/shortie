package com.kborowy.shortie.data.urls

import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortieUrl
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
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

    // todo: getAll paginated
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
}

private fun ResultRow.toShortieUrl(): ShortieUrl {
    return ShortieUrl(
        shortCode = ShortCode(this[UrlsTable.shortCode]),
        originalUrl = OriginalUrl(this[UrlsTable.originalUrl]),
        protected = this[UrlsTable.passwordHash] != null,
        expiryDate = this[UrlsTable.expiryDate],
    )
}
