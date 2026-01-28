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
package shortie.tests.data

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.data.urls.ShortieUrlTotals
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.extensions.toLocalDateTimeUTC
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.models.ShortieUrl
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import shortie.tests.DatabaseUtils
import shortie.tests.DatabaseUtils.clearDatabase
import shortie.tests.FakeClock
import shortie.tests.FakeShortCodeGenerator

class UrlsRepositoryTest {

    val db = DatabaseUtils.getTestDatabase()
    val coder = FakeShortCodeGenerator

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

    @Test
    fun `paginated results sizes and limits`() = runTest {
        val urls = mutableListOf<ShortieUrl>()
        for (i in 0..<10) {
            val url = OriginalUrl("https://www.$i.com")
            val code = coder.generateShortCode(i.toLong())
            urls.add(repo.insert(url, code))
        }

        urls.reverse() // reverse to test input
        var result = repo.getPageCursor(5)
        assertEquals(5, result.data.size)
        assertTrue(result.hasNext)
        assertNotNull(result.nextCursor)

        // order
        assertEquals(urls[0], result.data.first())
        assertEquals(urls[4], result.data.last())

        // get next batch
        result = repo.getPageCursor(3, nextCursor = result.nextCursor)
        assertEquals(3, result.data.size)
        assertTrue(result.hasNext)
        assertNotNull(result.nextCursor)

        // order
        assertEquals(urls[5], result.data.first())
        assertEquals(urls[7], result.data.last())

        // get last batch
        result = repo.getPageCursor(5, nextCursor = result.nextCursor)
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

        val result = repo.getPageCursor()
        assertEquals(max, result.data.size)
        assertFalse(result.hasNext)
        assertNull(result.nextCursor)

        // test DESC ordering
        assertEquals(urls.last(), result.data.first())
        assertEquals(urls.first(), result.data.last())
    }

    @Test
    fun `collects total overview`() = runTest {
        val clock = FakeClock()
        GlobalClockProvider.replaceClock(clock)

        val expired = Random.nextLong(2, 10)
        val active = Random.nextLong(2, 10)
        val total = expired + active

        clock.backward(3.days)
        for (i in 1..expired) {
            val url = OriginalUrl("https://www.example${i}.com")
            val code = ShortCode(value = "aDs3s${i}")
            repo.insert(
                url = url,
                code = code,
                expiry = LocalDateTime.now.asInstantUTC.plus(1.days).toLocalDateTimeUTC,
            )
        }

        for (i in 1..active) {

            val expiry: LocalDateTime? =
                if (i % 2 == 0L) Instant.now.plus(6.days).toLocalDateTimeUTC else null

            repo.insert(
                url = OriginalUrl("https://www.example-active-${i}.com"),
                code = ShortCode(value = "active-${i}"),
                expiry = expiry,
            )
        }

        clock.reset()
        val result = repo.getLinksTotals(LocalDateTime.now)
        assertEquals(ShortieUrlTotals(total = total, expired = expired, active = active), result)
        // make sure this make sense
        assertEquals(result.expired + result.active, result.total)
    }

}


