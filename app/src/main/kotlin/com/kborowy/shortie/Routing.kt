package com.kborowy.shortie

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureRouting() {
    val db2: Database by dependencies
    routing { get("/") { call.respondText("Hello World!") } }
}
