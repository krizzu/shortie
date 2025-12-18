package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.InternalServerError
import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.extensions.receiveOrThrow
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.plugins.withAdminAuth
import com.kborowy.shortie.services.UrlsService
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.io.encoding.Base64
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

                /** Return paginated query */
                get {
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 35
                    val cursor =
                        call.request.queryParameters["cursor"]?.let {
                            ShortCode(Base64.decode(it).toString())
                        }

                    val data =
                        service.getShortCodes(limit, cursor)
                            ?: throw InternalServerError("Could not read data")

                    val nextCursor =
                        data.nextCursor?.let { Base64.encode(it.shortCode.toByteArray()) }

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
                            nextCursor = nextCursor,
                        )
                    )
                }
            }
        }
    }
}
