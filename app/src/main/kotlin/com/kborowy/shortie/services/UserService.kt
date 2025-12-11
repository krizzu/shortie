package com.kborowy.shortie.services

import com.kborowy.shortie.data.users.UserRepository
import com.kborowy.shortie.errors.UnauthorizedHttpError
import com.kborowy.shortie.utils.JwtToken
import com.kborowy.shortie.utils.JwtVerifier
import com.kborowy.shortie.utils.PasswordHasher
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

data class AuthTokens(val access: JwtToken, val refresh: JwtToken)

interface UserService {
    suspend fun verifyAdminPassword(password: String): Boolean

    suspend fun issueNewTokens(): AuthTokens

    suspend fun refreshTokens(refreshToken: String): AuthTokens
}

fun UserService(repo: UserRepository, jwt: JwtVerifier): UserService = RealUserService(repo, jwt)

private class RealUserService(private val repo: UserRepository, private val jwt: JwtVerifier) :
    UserService {
    override suspend fun verifyAdminPassword(password: String): Boolean {
        val adminHash = repo.getAdminPassword()
        return PasswordHasher.verify(password = password, hash = adminHash)
    }

    override suspend fun issueNewTokens(): AuthTokens {
        val access = jwt.issueNewToken(JwtToken.TokenType.Access, 15.minutes)
        val refresh = jwt.issueNewToken(type = JwtToken.TokenType.Refresh, expiry = 90.days)
        return AuthTokens(access = access, refresh = refresh)
    }

    override suspend fun refreshTokens(refreshToken: String): AuthTokens {

        val token = jwt.verifyToken(refreshToken)
        jwt.validateCredentials(token, type = JwtToken.TokenType.Refresh)
            ?: UnauthorizedHttpError("Provided token is not valid (token=${token})")

        return issueNewTokens()
    }
}
