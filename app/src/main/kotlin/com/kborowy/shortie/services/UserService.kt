package com.kborowy.shortie.services

import com.kborowy.shortie.data.users.UserRepository
import com.kborowy.shortie.errors.TokenExpiredError
import com.kborowy.shortie.errors.TokenVerificationError
import com.kborowy.shortie.errors.UnauthorizedHttpError
import com.kborowy.shortie.utils.JwtToken
import com.kborowy.shortie.utils.JwtVerifier
import com.kborowy.shortie.utils.PasswordHasher
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class AuthTokens(val access: JwtToken, val refresh: JwtToken)

interface UserService {
    suspend fun verifyAdminPassword(password: String): Boolean

    fun issueNewTokens(): AuthTokens

    fun refreshTokens(refreshToken: String): AuthTokens
}

fun UserService(
    repo: UserRepository,
    jwt: JwtVerifier,
    log: Logger = LoggerFactory.getLogger("UserService"),
): UserService = RealUserService(repo, jwt, log)

private class RealUserService(
    private val repo: UserRepository,
    private val jwt: JwtVerifier,
    private val log: Logger,
) : UserService {

    override suspend fun verifyAdminPassword(password: String): Boolean {
        val adminHash = repo.getAdminPassword()
        return PasswordHasher.verify(password = password, hash = adminHash)
    }

    override fun issueNewTokens(): AuthTokens {
        val access = jwt.issueNewToken(JwtToken.TokenType.Access, 15.minutes)
        val refresh = jwt.issueNewToken(type = JwtToken.TokenType.Refresh, expiry = 90.days)
        return AuthTokens(access = access, refresh = refresh)
    }

    override fun refreshTokens(refreshToken: String): AuthTokens {
        log.info("begin token refresh")
        val token =
            try {
                jwt.verifyToken(refreshToken)
            } catch (_: TokenExpiredError) {
                log.error("token has expired (token=$refreshToken)")
                throw UnauthorizedHttpError("token has expired")
            } catch (_: TokenVerificationError) {
                log.error("token failed verification (token=$refreshToken)")
                throw UnauthorizedHttpError("token has expired")
            } catch (e: Exception) {
                log.error("token verification in shambles", e)
                throw UnauthorizedHttpError(
                    e.message ?: "something went wrong while validating token"
                )
            }
        jwt.validateCredentials(token, type = JwtToken.TokenType.Refresh)
            ?: run {
                log.error("token failed validation (token=$token")
                throw UnauthorizedHttpError("provided token is not valid (token=$token)")
            }

        log.info("token valid, refreshing")
        return issueNewTokens()
    }
}
