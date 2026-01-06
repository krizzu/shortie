package com.kborowy.shortie.routes.auth

import com.kborowy.shortie.data.users.DEFAULT_ADMIN_NAME
import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.UnauthorizedHttpError
import com.kborowy.shortie.extensions.receiveNullableCatching
import com.kborowy.shortie.plugins.withAdminAuth
import com.kborowy.shortie.services.UserService
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Application.authRouting() {
    routing {
        val service by inject<UserService>()
        val log = LoggerFactory.getLogger("AuthRoute")

        post("/auth/login") {
            // todo: how to handle when received payload is missing required keys?
            // sent: userName, required user
            val request =
                call.receiveNullableCatching<LoginPayloadDTO>()
                    ?: throw BadRequestError("invalid request, required {user, password}")

            if (request.user != DEFAULT_ADMIN_NAME) {
                log.error("unknown user requested to log in: ${request.user}")
                throw BadRequestError("unknown user ${request.user}")
            }

            if (!service.verifyAdminPassword(request.password)) {
                log.error("bad password for admin: ${request.user}")
                throw UnauthorizedHttpError("bad password")
            }

            log.info("issuing new tokens")
            val tokens = service.issueNewTokens()
            call.respond(
                TokenResponseDTO(
                    accessToken = tokens.access.value,
                    refreshToken = tokens.refresh.value,
                )
            )
        }

        post("/auth/refresh") {
            val request =
                call.receiveNullableCatching<TokenRefreshPayloadDTO>()
                    ?: throw BadRequestError("invalid payload")

            val tokens = service.refreshTokens(request.refreshToken)
            call.respond(
                TokenResponseDTO(
                    accessToken = tokens.access.value,
                    refreshToken = tokens.refresh.value,
                )
            )
        }

        // an endpoint to validate if access token is still valid
        withAdminAuth { get("/auth/validate") { call.respond(TokenValidResponseDTO(true)) } }
    }
}
