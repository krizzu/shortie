package com.kborowy.shortie.services.urls

import com.kborowy.shortie.data.counter.GlobalCounter
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.errors.AliasAlreadyExistsError
import com.kborowy.shortie.errors.ExpiryInPastError
import com.kborowy.shortie.errors.UnexpectedAppError
import com.kborowy.shortie.extensions.isInPast
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortiePageCursor
import com.kborowy.shortie.models.ShortieUrl
import com.kborowy.shortie.utils.PasswordHasher
import com.kborowy.shortie.utils.ShortCodeGenerator
import io.ktor.utils.io.CancellationException
import java.nio.ByteBuffer
import kotlin.io.encoding.Base64
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory

fun UrlsService(
    repo: UrlsRepository,
    counter: GlobalCounter,
    coder: ShortCodeGenerator,
): UrlsService = RealUrlsService(repo, counter, coder)

interface UrlsService {
    suspend fun generateShortie(
        url: OriginalUrl,
        expiry: LocalDateTime? = null,
        alias: String? = null,
        password: String? = null,
    ): ShortieUrl

    suspend fun resolveShortCode(code: ShortCode): ShortieUrl?

    suspend fun resolveShortCode(code: String): ShortieUrl?

    suspend fun verifyPassword(shortCode: ShortCode, password: String): Boolean

    suspend fun getShorties(limit: Int, nextCursor: String?): ShortieUrlPaginatedEncoded?
}

private class RealUrlsService(
    private val repo: UrlsRepository,
    private val counter: GlobalCounter,
    private val coder: ShortCodeGenerator,
) : UrlsService {
    private val log = LoggerFactory.getLogger("UrlsService")

    override suspend fun generateShortie(
        url: OriginalUrl,
        expiry: LocalDateTime?,
        alias: String?,
        password: String?,
    ): ShortieUrl {
        expiry?.let {
            if (it.isInPast) {
                log.warn("expiry date cannot be in the past (provided=$it)")
                throw ExpiryInPastError("expiry date cannot be in the past (provided=$it)")
            }
        }

        val passwordHash = password?.let { PasswordHasher.hash(it) }
        val shortCode = alias?.let { ShortCode(it) } ?: createHash()
        try {
            val shortie =
                repo.insert(url = url, code = shortCode, expiry = expiry, hash = passwordHash)
            return shortie
        } catch (e: CancellationException) {
            log.info("generation was cancelled")
            throw e
        } catch (e: ExposedSQLException) {
            log.error("failed to generate shortie", e)
            if (e.cause is PSQLException && (e.sqlState == "23505" || e.errorCode == 23505)) {
                throw AliasAlreadyExistsError()
            } else if (e.errorCode == 23505) {
                throw AliasAlreadyExistsError()
            }

            throw UnexpectedAppError("Database operation failed: ${e.message}")
        }
    }

    override suspend fun resolveShortCode(code: ShortCode): ShortieUrl? {
        return repo.get(code)
    }

    override suspend fun resolveShortCode(code: String): ShortieUrl? {
        try {
            val shortCode = ShortCode(code)
            return resolveShortCode(shortCode)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error("Cannot resolve short code $code", e)
            return null
        }
    }

    override suspend fun verifyPassword(shortCode: ShortCode, password: String): Boolean {
        val shortieHash = repo.getHashForCode(shortCode)
        requireNotNull(shortieHash) { "code \"${shortCode.value}\" not found" }
        val result = PasswordHasher.verify(password, shortieHash)
        log.info("verified password for short code ${shortCode.value} = $result")

        return result
    }

    override suspend fun getShorties(limit: Int, nextCursor: String?): ShortieUrlPaginatedEncoded? {
        val cursor =
            nextCursor?.let {
                // return null as whole operation
                decodeCursor(nextCursor) ?: return null
            }

        try {
            val result = repo.getPage(limit, cursor)
            return ShortieUrlPaginatedEncoded(
                data = result.data,
                hasNext = result.hasNext,
                nextCursor = result.nextCursor?.let { encodeCursor(it) },
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error("cannot get paginated result (limit=$limit, nextCursor=$nextCursor)", e)
            return null
        }
    }

    private suspend fun createHash(): ShortCode {
        val id = counter.getNextId()
        return coder.generateShortCode(id)
    }

    private fun encodeCursor(cursor: ShortiePageCursor): String {
        val buffer =
            ByteBuffer.allocate(16)
                .apply {
                    putLong(cursor.createdAt.toEpochMilliseconds())
                    putLong(cursor.id)
                }
                .array()

        return Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(buffer)
    }

    private fun decodeCursor(encoded: String): ShortiePageCursor? {
        try {
            val decoded = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).decode(encoded)
            val buffer = ByteBuffer.wrap(decoded)
            return ShortiePageCursor(
                createdAt = Instant.fromEpochMilliseconds(buffer.getLong()),
                id = buffer.getLong(),
            )
        } catch (e: Exception) {
            log.error("failed to decode cursor (cursor=$encoded)", e)
            return null
        }
    }
}
