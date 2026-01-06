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
