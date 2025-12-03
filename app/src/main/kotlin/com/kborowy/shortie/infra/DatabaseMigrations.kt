package com.kborowy.shortie.infra

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.annotations.Property
import org.flywaydb.core.Flyway

const val MIGRATIONS_DIRECTORY = "src/main/resources/migrations"

private fun createMigration(
    @Property("database.url") url: String,
    @Property("database.user") user: String,
    @Property("database.password") pass: String,
): Flyway =
    Flyway.configure()
        .apply {
            dataSource(url, user, pass)
            locations("classpath:migrations")
            baselineOnMigrate(true)
        }
        .load()

fun Application.runDatabaseMigrations() {
    val url = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val pass = environment.config.property("database.password").getString()
    val flyway = createMigration(url = url, user = user, pass = pass)
    flyway.migrate()
}
