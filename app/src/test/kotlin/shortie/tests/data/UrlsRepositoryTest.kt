package shortie.tests.data

import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import shortie.tests.DatabaseUtils
import shortie.tests.DatabaseUtils.clearDatabase

class UrlsRepositoryTest {

    val db = DatabaseUtils.getTestDatabase()
    val repo = UrlsRepository(db)

    init {
        DatabaseUtils.initDatabase()
    }

    @BeforeTest
    fun beforeEach() {
        clearDatabase(db)
    }

    @Test
    fun `stores and retrieves single shortie`() = runTest {
        val url = OriginalUrl("https://www.example.com")
        val code = ShortCode(value = "aDs3s1")
        val insertResult = repo.insert(url = url, code = code)

        assertNotNull(insertResult, "inserted shortie is null")
        assertEquals(code, insertResult.shortCode)
        assertEquals(url, insertResult.originalUrl)

        val getResult = repo.get(insertResult.shortCode)
        assertNotNull(getResult, "read shortie as null")
        assertEquals(insertResult, getResult, "inserted and retrieved shorties are diff")
    }

    @Test
    fun `removes single url`() = runTest {
        val url = OriginalUrl("https://www.example2.com")
        val code = ShortCode(value = "aDs3s1")
        val result = repo.insert(url, code, expiry = LocalDateTime.now, "hashed")

        assertNotNull(result, "inserted shortie is null")
        assertNotNull(repo.get(result.shortCode), "stored shortie is missing")

        repo.remove(result.shortCode)

        assertNull(repo.get(result.shortCode), "stored shortie has not been removed")
    }
}
