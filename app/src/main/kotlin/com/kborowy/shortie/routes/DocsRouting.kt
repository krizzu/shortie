package com.kborowy.shortie.routes

import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.docsRouting() {
    routing { swaggerUI("/docs", swaggerFile = "openapi/documentation.json") }
}
