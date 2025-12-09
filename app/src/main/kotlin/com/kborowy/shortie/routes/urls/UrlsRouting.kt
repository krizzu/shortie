package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.services.urls.UrlsService
import io.ktor.server.application.Application
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.urlsRouting() {
    routing {
        val service by inject<UrlsService>()

        // todo: auth only
        route("/urls") {
            post {
                val body =
                    call.receiveNullable<GenerateShortieDTO>()
                        ?: throw BadRequestError("missing required body")

                val shortie =
                    service.generateShortie(
                        url = OriginalUrl(body.url),
                        expiry = body.expiryDate,
                        alias = body.alias,
                        password = body.password,
                    )

                call.respond(GenerateShortieResponseDTO(shortCode = shortie.shortCode.value))
            }
        }
    }
}
