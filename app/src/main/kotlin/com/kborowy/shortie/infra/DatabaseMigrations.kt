package com.kborowy.shortie.infra

import io.ktor.server.plugins.di.annotations.Property
import org.flywaydb.core.Flyway

const val MIGRATIONS_DIRECTORY = "src/main/resources/migrations"

@Suppress("unused") // DI provider via application.yaml
fun provideMigration(
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
