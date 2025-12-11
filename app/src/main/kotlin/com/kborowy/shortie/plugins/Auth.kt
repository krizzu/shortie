package com.kborowy.shortie.plugins

import com.kborowy.shortie.errors.UnauthorizedHttpError
import com.kborowy.shortie.utils.JwtToken
import com.kborowy.shortie.utils.JwtVerifier
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import org.koin.ktor.ext.inject

private const val providerName: String = "admin"

fun Application.configureAuth() {
    val jwtVerifier by inject<JwtVerifier>()
    install(Authentication) {
        jwt(providerName) {
            realm = jwtVerifier.realm
            verifier(jwtVerifier)
            validate { credentials ->
                jwtVerifier.validateCredentials(credentials, JwtToken.TokenType.Access)
            }
            challenge { _, realm ->
                throw UnauthorizedHttpError("JWT is invalid or expired for $realm")
            }
        }
    }
}

fun Routing.withAdminAuth(block: Route.() -> Unit) {
    authenticate(providerName, build = block)
}
