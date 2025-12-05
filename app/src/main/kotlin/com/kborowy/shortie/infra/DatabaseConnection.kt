package com.kborowy.shortie.infra

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.util.logging.KtorSimpleLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.core.module.Module
import org.koin.dsl.module

private val log = KtorSimpleLogger("DatabaseConnection")

private fun createConnection(url: String, user: String, pass: String): Database {
    val config =
        HikariConfig().apply {
            jdbcUrl = url
            driverClassName = "org.postgresql.Driver"
            username = user
            password = pass
            maximumPoolSize = 10
        }

    val dbSource = HikariDataSource(config)

    log.info("Created connection to database")
    return Database.connect(datasource = dbSource)
}

fun Application.provideDatabaseDIModule(): Module {
    val url = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val pass = environment.config.property("database.password").getString()
    return module { single<Database> { createConnection(url = url, user = user, pass = pass) } }
}
