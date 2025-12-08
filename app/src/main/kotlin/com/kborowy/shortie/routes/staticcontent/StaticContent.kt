package com.kborowy.shortie.routes.staticcontent

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing

fun Application.configureStaticContent() {
    routing { staticResources("/assets", "assets") }
}
