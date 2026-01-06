package com.kborowy.shortie.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        val j by this@configureContentNegotiation.inject<Json>()
        json(j)
    }
}
