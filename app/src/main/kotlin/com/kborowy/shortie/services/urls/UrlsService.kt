package com.kborowy.shortie.services.urls

import com.kborowy.shortie.data.counter.GlobalCounter
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.errors.AliasAlreadyExistsError
import com.kborowy.shortie.errors.ExpiryInPastError
import com.kborowy.shortie.errors.UnexpectedAppError
import com.kborowy.shortie.extensions.isInPast
import com.kborowy.shortie.migrations.com.kborowy.shortie.utils.IdGenerator
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortieUrl
import com.kborowy.shortie.utils.PasswordHasher
import io.ktor.utils.io.CancellationException
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.slf4j.LoggerFactory

fun UrlsService(repo: UrlsRepository, counter: GlobalCounter, generator: IdGenerator): UrlsService =
    RealUrlsService(repo, counter, generator)

interface UrlsService {
    suspend fun generateShortie(
        url: OriginalUrl,
        expiry: LocalDateTime? = null,
        alias: String? = null,
        password: String? = null,
    ): ShortieUrl

    suspend fun resolveShortCode(code: ShortCode): ShortieUrl?

    suspend fun resolveShortCode(code: String): ShortieUrl?

    /** If shortie is protected, check if provided password match */
    suspend fun verifyShortCode(shortCode: ShortCode, password: String): Boolean
}

private class RealUrlsService(
    private val repo: UrlsRepository,
    private val counter: GlobalCounter,
    private val generator: IdGenerator,
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
            throw e
        } catch (e: ExposedSQLException) {
            if (e.errorCode == 23505) {
                throw AliasAlreadyExistsError()
            }
            throw UnexpectedAppError("Database operation failed: $e")
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
            throw UnexpectedAppError(e.message ?: "$e")
        }
    }

    override suspend fun verifyShortCode(shortCode: ShortCode, password: String): Boolean {
        val shortieHash = repo.getHashForCode(shortCode)
        requireNotNull(shortieHash) { "code \"${shortCode.value}\" not found" }
        val result = PasswordHasher.verify(password, shortieHash)
        log.info("verified password for short code ${shortCode.value} = $result")

        return result
    }

    private suspend fun createHash(): ShortCode {
        val id = counter.getNextId()
        return generator.generateShortCode(id)
    }
}
