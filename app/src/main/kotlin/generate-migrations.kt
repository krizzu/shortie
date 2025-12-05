import com.kborowy.shortie.data.urls.UrlsTable
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
            scriptDirectory = MIGRATIONS_DIRECTORY,
            scriptName = name,
            withLogs = true,
        )
    }
}
