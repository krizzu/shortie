/*
 * Copyright 2026 Krzysztof Borowy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kborowy.shortie.infra

import com.kborowy.shortie.data.users.DEFAULT_ADMIN_NAME
import com.kborowy.shortie.data.users.UsersTable
import com.kborowy.shortie.utils.PasswordHasher
import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.ktor.ext.inject

const val MIGRATIONS_DIRECTORY = "src/main/resources/migrations"

private fun createMigration(url: String, user: String, pass: String): Flyway =
    Flyway.configure()
        .dataSource(url, user, pass)
        /** note: flyway 11 has issue with .locations so sit at latest 10 for now */
        .locations("classpath:migrations")
        .validateMigrationNaming(true)
        .baselineOnMigrate(true)
        .load()

fun Application.runDatabaseMigrations() {

    // todo: construct db url by hand, rather than passing an extra url
    val url = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val pass = environment.config.property("database.password").getString()
    val flyway = createMigration(url = url, user = user, pass = pass)
    flyway.migrate()
    seedAdminUser()
}

// seed in admin with password if it's not existing
private fun Application.seedAdminUser() {
    val db by inject<Database>()
    val password = environment.config.property("admin.password").getString()

    transaction(db) {
        val exists =
            UsersTable.selectAll().where { UsersTable.name eq DEFAULT_ADMIN_NAME }.singleOrNull()
        if (exists == null) {
            val passwordHash = PasswordHasher.hash(password)
            UsersTable.insert {
                it[UsersTable.name] = DEFAULT_ADMIN_NAME
                it[UsersTable.password] = passwordHash
            }
        }
    }
}
