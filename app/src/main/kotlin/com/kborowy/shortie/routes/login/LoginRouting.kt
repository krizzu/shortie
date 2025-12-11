package com.kborowy.shortie.routes.login

import com.kborowy.shortie.data.users.DEFAULT_ADMIN_NAME
import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.UnauthorizedHttpError
import com.kborowy.shortie.services.UserService
import io.ktor.server.application.Application
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Application.loginRouting() {
    routing {
        val service by inject<UserService>()
        val log = LoggerFactory.getLogger("LoginRoute")
        post("/login") {
            val request =
                call.receiveNullable<LoginPayloadDTO>() ?: throw BadRequestError("missing body")

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
                LoginResponseDTO(
                    accessToken = tokens.access.value,
                    refreshToken = tokens.refresh.value,
                )
            )
        }
    }
}
