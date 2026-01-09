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
