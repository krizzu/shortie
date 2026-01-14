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
package com.kborowy.shortie.routes.redirect

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.GoneHttpError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.getOrFail
import com.kborowy.shortie.extensions.respondWithTemplate
import com.kborowy.shortie.models.ShortieUrl
import com.kborowy.shortie.plugins.HtmlTemplates
import com.kborowy.shortie.plugins.model
import com.kborowy.shortie.services.AnalyticService
import com.kborowy.shortie.services.urls.UrlsService
import io.ktor.http.appendPathSegments
import io.ktor.server.application.Application
import io.ktor.server.plugins.origin
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.redirectRouting() {
    routing {
        val service by inject<UrlsService>()
        val analytics by inject<AnalyticService>()
        val proxyPort: Int? by inject(qualifier = named("proxy_port"))
        /** "Shortie" resolving */
        route("/{short_code}") {

            /**
             * Decode given short code ("Shortie") and redirect user to final url
             * If shortie requires password, redirects to password page
             *
             * @path short_code the short code to decode
             * @response 302 Redirect to /password if url is protected
             * @response 302 Redirect to resolved URL
             */
            get {
                val shortie = getActiveShortie("short_code", service)

                if (shortie.protected) {
                    // redirect to /password, allow user enter password
                    return@get call.respondRedirect(permanent = false) {
                        port = proxyPort ?: call.request.origin.serverPort
                        host = call.request.origin.serverHost
                        appendPathSegments("password")
                    }
                }

                analytics.incrementClick(shortie)
                call.respondRedirect(shortie.originalUrl.value, permanent = false)
            }

            /** Shortie is protected, requires password to access */
            route("/password") {

                /** Render HTML page to allow user enter password */
                get {
                    val shortie = getActiveShortie("short_code", service)
                    call.respondWithTemplate(
                        HtmlTemplates.Password,
                        HtmlTemplates.Password.model(shortie.shortCode),
                    )
                }

                /**
                 * Receive a password and resolve to original url if resolved
                 *
                 * @path short_code the short code to decode
                 * @body application/x-www-form-urlencoded Form data containing password to resolve
                 *   Shortie
                 * @response 400 Password not found in form data
                 * @response 404 Shortie is not protected or password is not correct
                 * @response 302 Redirect to resolved URL
                 */
                post {
                    val shortie = getActiveShortie("short_code", service)
                    if (!shortie.protected) {
                        throw NotFoundHttpError("link not found")
                    }
                    val form = call.receiveParameters()
                    val password = form["password"] ?: throw BadRequestError("password missing")

                    if (service.verifyPassword(shortie.shortCode, password)) {
                        analytics.incrementClick(shortie)
                        call.respond<ShortiePasswordResponseDTO>(
                            ShortiePasswordResponseDTO(shortie.originalUrl.value)
                        )
                    } else {
                        throw NotFoundHttpError("link not found")
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

    val shortie = service.resolveShortCode(shortCode) ?: throw NotFoundHttpError("link not found")

    if (shortie.expired) {
        throw GoneHttpError("link expired")
    }

    return shortie
}
