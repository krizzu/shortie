package tests.repos

import com.kborowy.shortie.data.urls.UrlsTable
import com.kborowy.shortie.data.counter.GlobalCounter
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tests.tests.DatabaseUtils

class GlobalCounterTests {

    val db = DatabaseUtils.getTestDatabase()
    val counter = GlobalCounter(db)

    init {
        DatabaseUtils.initDatabase()
    }

    @BeforeTest
    fun beforeEach() {
        transaction(db) { UrlsTable.deleteAll() }
    }

    @Test
    fun increments() = runTest {
        assertEquals(1, counter.getNextId())
        assertEquals(2, counter.getNextId())
        assertEquals(3, counter.getNextId())
        assertEquals(4, counter.getNextId())
        assertEquals(5, counter.getNextId())
        counter.getNextId()
        counter.getNextId()
        counter.getNextId()
        counter.getNextId()
        assertEquals(10, counter.getNextId())
    }
}
