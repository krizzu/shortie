package com.kborowy.shortie.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing

fun Application.staticContentRouting() {
    routing { staticResources("/assets", "assets") }
}
