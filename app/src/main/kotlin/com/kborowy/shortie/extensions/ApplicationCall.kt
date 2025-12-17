package com.kborowy.shortie.extensions

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.plugins.HtmlTemplates
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.util.logging.error
import io.ktor.utils.io.CancellationException
import org.slf4j.LoggerFactory

suspend inline fun ApplicationCall.respondWithTemplate(
    template: HtmlTemplates,
    model: Any? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    this.respond(status = status, MustacheContent(template = template.file, model = model))
}

suspend inline fun <reified T> ApplicationCall.receiveNullableCatching(): T? {
    val logger = LoggerFactory.getLogger("ReceiveNullableCatching")
    return try {
        receiveNullable<T>()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        logger.error(e)
        null
    }
}

suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(): T {
    val logger = LoggerFactory.getLogger("receiveOrThrow")
    return try {
        receive<T>()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        logger.error(e)
        throw BadRequestError("invalid payload: ${e.message ?: ""}")
    }
}
