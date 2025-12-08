package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.GoneHttpError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.getOrFail
import com.kborowy.shortie.extensions.respondWithTemplate
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortieUrl
import com.kborowy.shortie.plugins.HtmlTemplates
import com.kborowy.shortie.services.urls.UrlsService
import io.ktor.http.appendPathSegments
import io.ktor.server.application.Application
import io.ktor.server.request.receiveNullable
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureUrlsRouting() {
    routing {
        route("/{short_code}") {
            get {
                val service by inject<UrlsService>()
                val shortie = getActiveShortie("short_code", service)

                if (shortie.protected) {
                    // redirect to allow user enter password
                    return@get call.respondRedirect(permanent = false) {
                        appendPathSegments("password")
                    }
                }

                call.respondRedirect(shortie.originalUrl.value, permanent = false)
            }

            route("/password") {
                get {
                    val service by inject<UrlsService>()
                    val shortie = getActiveShortie("short_code", service)
                    call.respondWithTemplate(
                        HtmlTemplates.Password,
                        mapOf("shortCode" to shortie.shortCode.value),
                    )
                }
                post {
                    val service by inject<UrlsService>()
                    val shortie = getActiveShortie("short_code", service)
                    if (!shortie.protected) {
                        throw NotFoundHttpError("${shortie.shortCode} not found")
                    }
                    val form = call.receiveParameters()
                    val password = form["password"] ?: throw BadRequestError("password missing")

                    if (service.verifyShortCode(shortie.shortCode, password)) {
                        call.respondRedirect(shortie.originalUrl.value, permanent = false)
                    } else {
                        throw NotFoundHttpError("${shortie.shortCode} not found")
                    }
                }
            }
        }

        // todo: auth only
        route("/urls") {
            post {
                val service by inject<UrlsService>()
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

// reads shortie from params and check if it's valid and not expired
private suspend fun RoutingContext.getActiveShortie(
    paramName: String,
    service: UrlsService,
): ShortieUrl {
    val shortCode = call.parameters.getOrFail(paramName)

    val shortie =
        service.resolveShortCode(shortCode) ?: throw NotFoundHttpError("$shortCode not found")

    if (shortie.expired) {
        throw GoneHttpError("$shortCode expired")
    }

    return shortie
}
