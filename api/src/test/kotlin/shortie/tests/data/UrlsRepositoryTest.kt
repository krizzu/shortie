package shortie.tests.data

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortieUrl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import shortie.tests.DatabaseUtils
import shortie.tests.DatabaseUtils.clearDatabase
import shortie.tests.FakeClock
import shortie.tests.FakeShortCodeGenerator

class UrlsRepositoryTest {

    val db = DatabaseUtils.getTestDatabase()
    val coder = FakeShortCodeGenerator

    val repo = UrlsRepository(db, coder)

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

    @Test
    fun `paginated results sizes and limits`() = runTest {
        val urls = mutableListOf<ShortieUrl>()
        for (i in 0..<10) {
            val url = OriginalUrl("https://www.$i.com")
            val code = coder.generateShortCode(i.toLong())
            urls.add(repo.insert(url, code))
        }

        urls.reverse() // reverse to test input
        var result = repo.getPage(5)
        assertEquals(5, result.data.size)
        assertTrue(result.hasNext)
        assertNotNull(result.nextCursor)

        // order
        assertEquals(urls[0], result.data.first())
        assertEquals(urls[4], result.data.last())

        // get next batch
        result = repo.getPage(3, nextCursor = result.nextCursor)
        assertEquals(3, result.data.size)
        assertTrue(result.hasNext)
        assertNotNull(result.nextCursor)

        // order
        assertEquals(urls[5], result.data.first())
        assertEquals(urls[7], result.data.last())

        // get last batch
        result = repo.getPage(5, nextCursor = result.nextCursor)
        assertEquals(2, result.data.size)
        assertFalse(result.hasNext)
        assertNull(result.nextCursor)

        // order
        assertEquals(urls[8], result.data.first())
        assertEquals(urls[9], result.data.last())
    }

    @Test
    fun `order of paginated results`() = runTest {
        val clock = FakeClock()
        GlobalClockProvider.replaceClock(clock)
        val max = 20

        val urls = mutableListOf<ShortieUrl>()
        for (i in 1..max) {
            val url = OriginalUrl("https://www.$i.com")
            val code = coder.generateShortCode(i.toLong())
            urls.add(repo.insert(url, code))
            clock.forward(1.seconds)
        }

        val result = repo.getPage()
        assertEquals(max, result.data.size)
        assertFalse(result.hasNext)
        assertNull(result.nextCursor)

        // test DESC ordering
        assertEquals(urls.last(), result.data.first())
        assertEquals(urls.first(), result.data.last())
    }
}
