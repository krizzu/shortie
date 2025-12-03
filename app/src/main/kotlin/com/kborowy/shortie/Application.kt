package com.kborowy.shortie

import com.kborowy.shortie.data.DataDIModule
import com.kborowy.shortie.infra.provideDatabaseConnection
import com.kborowy.shortie.infra.runDatabaseMigrations
import com.kborowy.shortie.services.ServicesDIModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    EngineMain.main(args)
}

// main scaffold module
fun Application.app() {
    setupDI()
    runDatabaseMigrations()

    configureRouting()
}

fun Application.setupDI() {
    install(Koin) {
        slf4jLogger()

        modules(provideDatabaseConnection(), DataDIModule, ServicesDIModule)
    }
}
