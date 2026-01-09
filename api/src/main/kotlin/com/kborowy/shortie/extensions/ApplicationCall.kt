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
    val logger = LoggerFactory.getLogger("ReceiveOrThrow")
    return try {
        receive<T>()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        logger.error(e)
        throw BadRequestError("invalid payload: ${e.message ?: ""}")
    }
}
