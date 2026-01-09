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
package shortie.tests

import com.kborowy.shortie.data.urls.UrlsTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

private const val dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
private const val driver = "org.h2.Driver"

object DatabaseUtils {

    // preferably run once before tests
    fun initDatabase() {
        Flyway.configure()
            .apply {
                dataSource(dbUrl, "", "")
                locations("classpath:migrations")
                driver(driver)
                loggers() // silence logs, set "auto" to see them back
            }
            .load()
            .migrate()
    }

    fun getTestDatabase(): Database {
        return Database.connect(dbUrl, driver)
    }

    fun clearDatabase(db: Database) {
        transaction(db) { UrlsTable.deleteAll() }
    }
}
