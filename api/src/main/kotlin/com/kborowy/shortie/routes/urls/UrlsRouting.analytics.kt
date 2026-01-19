package com.kborowy.shortie.routes.urls

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.urlsAnalyticRouting() {

    get { call.respondText("hello") }
}
