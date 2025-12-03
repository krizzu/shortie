package com.kborowy.shortie.infra

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.plugins.di.annotations.Property
import io.ktor.util.logging.KtorSimpleLogger
import org.jetbrains.exposed.v1.jdbc.Database

private val log = KtorSimpleLogger("DatabaseConnection")



@Suppress("unused") // DI provider via application.yaml
fun provideDatabase(
    @Property("database.url") url: String,
    @Property("database.user") user: String,
    @Property("database.password") pass: String,
): Database {
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
