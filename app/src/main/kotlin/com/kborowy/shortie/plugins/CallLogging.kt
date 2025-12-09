package com.kborowy.shortie.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri

fun Application.configureCallLogging() {
    install(CallLogging) {
        format { call ->
            """ Call info:
                Status: ${call.response.status()}
                Method: ${call.request.httpMethod.value}
                Path:   ${call.request.uri}
            """
                .trimIndent()
        }
    }
}
