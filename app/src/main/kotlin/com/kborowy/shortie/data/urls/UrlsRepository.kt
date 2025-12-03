package com.kborowy.shortie.data.urls

import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortieUrl
import java.time.LocalDateTime
import org.jetbrains.exposed.v1.jdbc.Database

interface UrlsRepository {
    suspend fun createUrl(
        url: OriginalUrl,
        alias: String? = null,
        expiryDate: LocalDateTime? = null,
        password: String? = null,
    ): ShortieUrl

    suspend fun removeUrl(shortCode: ShortCode): Boolean

    suspend fun getUrl(shortCode: ShortCode): ShortieUrl?

    // todo: getAll paginated
}

fun UrlsRepository(db: Database): UrlsRepository = RealUrlsRepository(db)

// Implementation
private class RealUrlsRepository(private val db: Database) : UrlsRepository {
    override suspend fun createUrl(
        url: OriginalUrl,
        alias: String?,
        expiryDate: LocalDateTime?,
        password: String?,
    ): ShortieUrl {
        TODO("Not yet implemented")
    }

    override suspend fun removeUrl(shortCode: ShortCode): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUrl(shortCode: ShortCode): ShortieUrl? {
        TODO("Not yet implemented")
    }
}
