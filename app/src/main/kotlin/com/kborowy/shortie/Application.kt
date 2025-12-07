package com.kborowy.shortie

import com.kborowy.shortie.infra.runDatabaseMigrations
import com.kborowy.shortie.plugins.configureContentNegotiation
import com.kborowy.shortie.plugins.setupDI
import com.kborowy.shortie.plugins.setupStatusPages
import com.kborowy.shortie.routes.urls.configureUrlsRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

// main scaffold module
fun Application.app() {
    setupDI()
    setupStatusPages()
    configureContentNegotiation()

    runDatabaseMigrations()
    configureUrlsRouting()
}
