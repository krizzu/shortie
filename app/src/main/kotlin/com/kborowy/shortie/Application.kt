package com.kborowy.shortie

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.di.dependencies
import org.flywaydb.core.Flyway

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.app() {
    val migration: Flyway by dependencies

    migration.migrate()
    configureRouting()
}
