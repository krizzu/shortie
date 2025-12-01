package com.kborowy.shortie

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.di.dependencies
import org.jetbrains.exposed.v1.jdbc.Database

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.app() {
    configureRouting()

    val db1: Database by dependencies
    val db2: Database by dependencies
}
