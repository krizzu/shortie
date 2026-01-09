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
import com.kborowy.shortie.data.urls.UrlsTable
import com.kborowy.shortie.data.users.UsersTable
import com.kborowy.shortie.infra.MIGRATIONS_DIRECTORY
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

fun getConnection(): Database {
    val url = System.getenv("APP_DB_URL") ?: error("Missing env variable: APP_DB_URL")
    val user = System.getenv("APP_DB_USER") ?: error("Missing env variable: APP_DB_USER")
    val password =
        System.getenv("APP_DB_PASSWORD") ?: error("Missing env variable: APP_DB_PASSWORD")

    return Database.connect(
        url = url,
        driver = "org.postgresql.Driver",
        user = user,
        password = password,
    )
}

fun getMigrationName(): String {
    return System.getenv("MIGRATION_NAME") ?: error("Missing env variable: MIGRATION_NAME")
}

@OptIn(ExperimentalDatabaseMigrationApi::class)
fun main() {
    val db = getConnection()
    val name = getMigrationName()

    transaction(db) {
        MigrationUtils.generateMigrationScript(
            UrlsTable,
            UsersTable,
            scriptDirectory = MIGRATIONS_DIRECTORY,
            scriptName = name,
            withLogs = true,
        )
    }
}
