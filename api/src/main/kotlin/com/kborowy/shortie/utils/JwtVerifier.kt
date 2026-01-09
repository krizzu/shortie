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
package com.kborowy.shortie.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier as JWTVERIFIER
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.auth0.jwt.interfaces.Payload
import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.errors.TokenExpiredError
import com.kborowy.shortie.errors.TokenVerificationError
import com.kborowy.shortie.extensions.nowInstant
import io.ktor.server.auth.jwt.JWTCredential
import kotlin.io.encoding.Base64
import kotlin.time.Duration
import kotlin.time.toJavaInstant
import kotlinx.datetime.LocalDateTime
import org.slf4j.LoggerFactory

@JvmInline
value class JwtToken(val value: String) {
    enum class TokenType() {
        Access,
        Refresh,
    }

    data class TokenPayload(
        val type: TokenType,
        val subject: String,
        val issuer: String,
        val audience: String,
    )
}

private const val JwtSubject = "shortie-admin"
private const val JwtClaimType = "type"

interface JwtVerifier : JWTVerifier {

    val realm: String

    fun issueNewToken(type: JwtToken.TokenType, expiry: Duration): JwtToken

    /**
     * verifies if token is valid, not expired and not tempered with
     *
     * throws [TokenExpiredError] or [TokenVerificationError] when verification fails
     */
    fun verifyToken(token: JwtToken): JwtToken.TokenPayload

    /**
     * verifies if token is valid, not expired and not tempered with
     *
     * throws [TokenExpiredError] or [TokenVerificationError] when verification fails
     */
    fun verifyToken(tokenValue: String): JwtToken.TokenPayload

    /**
     * Extra check if token belongs to shortie api and if it's of required type
     *
     * return null if validation fails
     */
    fun validateCredentials(
        credentials: JWTCredential,
        type: JwtToken.TokenType,
    ): JwtToken.TokenPayload?

    /**
     * Extra check if token belongs to shortie api and if it's of required type
     *
     * return null if validation fails
     */
    fun validateCredentials(
        payload: JwtToken.TokenPayload,
        type: JwtToken.TokenType,
    ): JwtToken.TokenPayload?
}

fun JwtVerifier(pass: String, aud: String, iss: String, realm: String): JwtVerifier {
    val builder = JWT.require(Algorithm.HMAC256(pass)).withAudience(aud).withIssuer(iss)

    // a trick to get custom clock implementation in pace
    val verifier = (builder as JWTVERIFIER.BaseVerification).build(GlobalClockProvider.javaClock)

    return RealJwtVerifier(pass, aud, iss, realm, verifier)
}

private class RealJwtVerifier(
    val pass: String,
    val aud: String,
    val iss: String,
    override val realm: String,
    val verifier: JWTVerifier,
) : JwtVerifier, JWTVerifier by verifier {

    override fun issueNewToken(type: JwtToken.TokenType, expiry: Duration): JwtToken {

        val token =
            JWT.create()
                .withAudience(aud)
                .withIssuer(iss)
                .withSubject(JwtSubject)
                .withClaim(JwtClaimType, type.name)
                .withExpiresAt(LocalDateTime.nowInstant.plus(expiry).toJavaInstant())
                .sign(Algorithm.HMAC256(pass))

        return JwtToken(value = token)
    }

    override fun verifyToken(tokenValue: String): JwtToken.TokenPayload {
        try {
            return verify(tokenValue).toPayload()
                ?: throw TokenVerificationError("could not parse token")
        } catch (e: TokenExpiredException) {
            throw TokenExpiredError(e.message ?: "token has expired")
        } catch (e: JWTVerificationException) {
            throw TokenVerificationError(
                e.message ?: "Token verification failed for unknown reason"
            )
        }
    }

    override fun verifyToken(token: JwtToken): JwtToken.TokenPayload {
        return verifyToken(tokenValue = token.value)
    }

    override fun validateCredentials(
        credentials: JWTCredential,
        type: JwtToken.TokenType,
    ): JwtToken.TokenPayload? {
        val payload = credentials.toPayload() ?: return null
        return validateCredentials(payload = payload, type = type)
    }

    override fun validateCredentials(
        payload: JwtToken.TokenPayload,
        type: JwtToken.TokenType,
    ): JwtToken.TokenPayload? {
        if (payload.subject != JwtSubject) {
            return null
        }

        if (payload.type != type) {
            return null
        }

        return payload
    }
}

private fun JWTCredential.toPayload(): JwtToken.TokenPayload? {

    val type = this.getClaim(JwtClaimType, String::class) ?: return null
    val sub = this.subject ?: return null
    val iss = this.issuer ?: return null
    val aud = this.audience.firstOrNull() ?: return null

    return JwtToken.TokenPayload(
        type = JwtToken.TokenType.valueOf(type),
        subject = sub,
        issuer = iss,
        audience = aud,
    )
}

private fun Payload.toPayload(): JwtToken.TokenPayload {
    return JwtToken.TokenPayload(
        type = JwtToken.TokenType.valueOf(this.getClaim(JwtClaimType).asString()),
        subject = this.subject,
        issuer = this.issuer,
        audience = this.audience.first(),
    )
}

private fun DecodedJWT.toPayload(): JwtToken.TokenPayload? {
    try {
        val decoded = Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL).decode(this.payload)
        val parsed = JWTParser().parsePayload(String(decoded))
        return parsed.toPayload()
    } catch (e: Exception) {
        LoggerFactory.getLogger("JwtVerifier").error("Failed to parse token payload", e)
        return null
    }
}
