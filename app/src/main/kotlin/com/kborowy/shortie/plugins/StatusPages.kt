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
