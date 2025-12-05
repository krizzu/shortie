package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.NotFoundHttpErrors
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureUrlsRouting() {
    routing {
        route("/urls") {
            get("/{short_code}") {
                val shortCode =
                    call.parameters["short_code"]
                        ?: throw NotFoundHttpErrors("short_code is missing from url")

                call.respondText("todo: handle $shortCode")
            }

            get("/{short_code}/password") {
                val shortCode =
                    call.parameters["short_code"]
                        ?: throw NotFoundHttpErrors("short_code is missing from url")

                call.respondText("todo: response with html page to enter the password")
            }
        }
    }
}
