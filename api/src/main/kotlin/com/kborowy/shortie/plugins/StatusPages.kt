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

import com.kborowy.shortie.errors.AppError
import com.kborowy.shortie.errors.AppHttpError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.respondWithTemplate
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import org.slf4j.LoggerFactory

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppHttpError> { call, cause ->
            if (cause is NotFoundHttpError) {
                call.respondWithTemplate(HtmlTemplates.NotFound, status = HttpStatusCode.NotFound)
            } else {
                call.respondText(text = "${cause.message}", status = cause.statusCode)
            }
        }

        exception<AppError> { call, cause ->
            LoggerFactory.getLogger("InternalError")
                .error("Uncaught exception: ${cause.message} on ${call.request.path()}", cause)

            call.respondWithTemplate(
                HtmlTemplates.ServerError,
                status = HttpStatusCode.InternalServerError,
            )
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondWithTemplate(HtmlTemplates.NotFound, status = HttpStatusCode.NotFound)
        }

        exception<Throwable> { call, cause ->
            LoggerFactory.getLogger("SeriousException")
                .error("Uncaught exception: ${cause.message} on ${call.request.path()}", cause)

            call.respondWithTemplate(
                HtmlTemplates.ServerError,
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
}
