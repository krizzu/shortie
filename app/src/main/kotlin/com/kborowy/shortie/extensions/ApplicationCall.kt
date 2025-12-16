package com.kborowy.shortie.extensions

import com.kborowy.shortie.plugins.HtmlTemplates
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.utils.io.CancellationException

suspend inline fun ApplicationCall.respondWithTemplate(
    template: HtmlTemplates,
    model: Any? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    this.respond(status = status, MustacheContent(template = template.file, model = model))
}

suspend inline fun <reified T> ApplicationCall.receiveNullableCatching(): T? {
    return try {
        receiveNullable<T>()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}
