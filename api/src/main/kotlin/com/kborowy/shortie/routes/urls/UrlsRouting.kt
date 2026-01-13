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
package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.extensions.receiveOrThrow
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.plugins.withAdminAuth
import com.kborowy.shortie.services.urls.UrlsService
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.ktor.ext.inject

fun Application.urlsRouting() {
    routing {
        withAdminAuth {
            val service by inject<UrlsService>()
            route("/urls") {

                /**
                 * admin only Creates a short url out of provided url and options
                 *
                 * @response 400 if bad payload is provided
                 */
                post {
                    val body = call.receiveOrThrow<GenerateShortiePayloadDTO>()

                    val shortie =
                        service.generateShortie(
                            url = OriginalUrl(body.url),
                            expiry = body.expiryDate?.toLocalDateTime(TimeZone.UTC),
                            alias = body.alias,
                            password = body.password,
                        )

                    call.respond(GenerateShortieResponseDTO(shortCode = shortie.shortCode.value))
                }

                /** allow to bulk delete shorties */
                delete {
                    val body = call.receiveOrThrow<DeleteShortiePayloadDTO>()
                    val removed = service.removeShorties(body.shortCodes.map { ShortCode(it) })
                    call.respond(DeleteShortieResponseDTO(deleted = removed))
                }

                /** keyset paginated query */
                get {
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                    val cursor = call.request.queryParameters["cursor"]

                    val data =
                        service.getShorties(limit, cursor)
                            ?: throw BadRequestError("Could not decode cursor")

                    call.respond(
                        PaginatedShortieResponseDTO(
                            data =
                                data.data.map {
                                    ShortieDTO(
                                        shortCode = it.shortCode.value,
                                        originalUrl = it.originalUrl.value,
                                        protected = it.protected,
                                        expiryDate = it.expiryDate?.asInstantUTC,
                                    )
                                },
                            hasNext = data.hasNext,
                            nextCursor = data.nextCursor,
                        )
                    )
                }
            }
        }
    }
}
