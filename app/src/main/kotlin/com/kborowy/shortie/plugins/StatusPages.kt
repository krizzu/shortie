package com.kborowy.shortie.plugins

import com.kborowy.shortie.errors.AppError
import com.kborowy.shortie.errors.AppHttpError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.respondWithTemplate
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import org.slf4j.LoggerFactory

fun Application.setupStatusPages() {
    install(StatusPages) {
        exception<AppHttpError> { call, cause ->
            if (cause is NotFoundHttpError) {
                call.respondWithTemplate(
                    HtmlTemplates.NotFound,
                    HtmlTemplates.NotFound.model(page = call.request.local.uri),
                )
            } else {
                call.respondText(
                    text = "${cause.statusCode}: ${cause.message}",
                    status = cause.statusCode,
                )
            }
        }

        exception<AppError> { call, cause ->
            call.respondText(text = "Request failed: ${cause::class}: ${cause.message}")
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondWithTemplate(
                HtmlTemplates.NotFound,
                HtmlTemplates.NotFound.model(page = call.request.local.uri),
            )
        }

        exception<Throwable> { call, cause ->
            LoggerFactory.getLogger("SeriousException").warn("Uncaught error: ${cause.message}")

            call.respondText(
                "500: Something went wrong inside :>",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
}
