package com.kborowy.shortie.routes

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.GoneHttpError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.getOrFail
import com.kborowy.shortie.extensions.respondWithTemplate
import com.kborowy.shortie.models.ShortieUrl
import com.kborowy.shortie.plugins.HtmlTemplates
import com.kborowy.shortie.services.UrlsService
import io.ktor.http.appendPathSegments
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.shortCodeRouting() {
    routing {
        val service by inject<UrlsService>()
        /** "Shortie" resolving */
        route("/{short_code}") {

            /**
             * Decode given short code ("Shortie")
             *
             * @path short_code the short code to decode
             * @response 302 Redirect to /password if url is protected
             * @response 302 Redirect to resolved URL
             */
            get {
                val shortie = getActiveShortie("short_code", service)

                if (shortie.protected) {
                    // redirect to allow user enter password
                    return@get call.respondRedirect(permanent = false) {
                        appendPathSegments("password")
                    }
                }

                call.respondRedirect(shortie.originalUrl.value, permanent = false)
            }

            /** Shortie is protected, requires password to access */
            route("/password") {

                /** Render HTML page to allow user enter password */
                get {
                    val shortie = getActiveShortie("short_code", service)
                    call.respondWithTemplate(
                        HtmlTemplates.Password,
                        mapOf("shortCode" to shortie.shortCode.value),
                    )
                }

                /**
                 * @path short_code the short code to decode
                 * @body application/x-www-form-urlencoded Form data containing password to resolve Shortie
                 * @response 400 Password not found in form data
                 * @response 404 Shortie is not protected or password is not correct
                 * @response 302 Redirect to resolved URL
                 */
                post {
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
