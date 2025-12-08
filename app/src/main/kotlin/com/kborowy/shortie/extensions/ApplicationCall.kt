package com.kborowy.shortie.extensions

import com.kborowy.shortie.plugins.HtmlTemplates
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.response.respond

suspend inline fun ApplicationCall.respondWithTemplate(
    template: HtmlTemplates,
    model: Any? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    this.respond(status = status, MustacheContent(template = template.file, model = model))
}
