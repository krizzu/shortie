package com.kborowy.shortie.plugins

import com.kborowy.shortie.errors.AppHttpError
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.setupStatusPages() {
    install(StatusPages) {
        exception<AppHttpError> { call, cause ->
            call.respondText(
                text = "${cause.statusCode}: ${cause.message}",
                status = cause.statusCode,
            )
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondText(
                "<h1>Page Not Found</h1>",
                ContentType.Text.Html,
                HttpStatusCode.NotFound,
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText("500: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }
    }
}
