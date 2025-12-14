package com.kborowy.shortie.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import org.slf4j.LoggerFactory

fun Application.configureCORS() {
    val log = LoggerFactory.getLogger("CORS plugin")
    val enabled = environment.config.property("ktor.allowCORS").getString().lowercase() == "true"
    if (enabled) {
        log.info("CORS plugin enabled")
        install(CORS) {
            anyMethod()
            allowHeader("Authorization")
            allowNonSimpleContentTypes = true
            anyHost()
        }
    } else {
        log.info("CORS plugin disabled")
    }
}
