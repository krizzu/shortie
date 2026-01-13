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
@file:OptIn(ExperimentalTime::class)

package shortie.tests.services

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.data.counter.GlobalCounter
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.errors.AliasAlreadyExistsError
import com.kborowy.shortie.errors.ExpiryInPastError
import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.extensions.nowInstant
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import com.kborowy.shortie.services.urls.UrlsService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import shortie.tests.DatabaseUtils
import shortie.tests.DatabaseUtils.clearDatabase
import shortie.tests.FakeClock
import shortie.tests.FakeShortCodeGenerator

class UrlsServiceTest {

    val db = DatabaseUtils.getTestDatabase()
    val repo = UrlsRepository(db)
    val service =
        UrlsService(repo = repo, counter = FakeGlobalCounter(), coder = FakeShortCodeGenerator)

    init {
        DatabaseUtils.initDatabase()
    }

    @BeforeTest
    fun beforeEach() {
        clearDatabase(db)
        GlobalClockProvider.resetClock()
    }

    @Test
    fun `generates basic shortie`() = runTest {
        val url = OriginalUrl("https://www.example.com")
        val shortie = service.generateShortie(url = url)
        assertEquals(3, shortie.shortCode.value.length)
        assertFalse(shortie.expired)
        assertFalse(shortie.protected)
    }

    @Test
    fun `generates shortie with expiration date`() = runTest {
        val clock = FakeClock()
        GlobalClockProvider.replaceClock(clock)

        val url = OriginalUrl("https://www.example2.com")
        val twoDays = LocalDateTime.nowInstant.plus(2.days).toLocalDateTime(TimeZone.UTC)
        val shortie = service.generateShortie(url = url, expiry = twoDays)
        assertEquals(3, shortie.shortCode.value.length)
        assertFalse(shortie.expired)
        clock.forward(1.days)
        clock.forward(23.hours)
        clock.forward(59.minutes)
        assertFalse(
            shortie.expired,
            "should be not be expired just yet (shortie=${shortie.expiryDate}, now=${LocalDateTime.now})",
        )

        clock.forward(1.minutes)
        assertTrue(
            shortie.expired,
            "should be expired by now (shortie=${shortie.expiryDate}, now=${LocalDateTime.now})",
        )
    }

    @Test
    fun `generates shortie with password`() = runTest {
        val url = OriginalUrl("https://www.example3.com")
        val password = "my_secret"
        val shortie = service.generateShortie(url = url, password = password)
        assertTrue(shortie.protected, "shortie had a password so should be protected")

        assertFalse("passwords should not match") {
            service.verifyPassword(shortie.shortCode, "my wrong password")
        }

        assertTrue("passwords should match") { service.verifyPassword(shortie.shortCode, password) }
    }

    @Test
    fun `reject shortie with same alias`() = runTest {
        val url = OriginalUrl("https://www.example3.com")
        val myAlias = "my-short"
        val shortie = service.generateShortie(url = url, alias = myAlias)
        assertNotNull(shortie, "shortie not created")

        assertFailsWith(AliasAlreadyExistsError::class) {
            service.generateShortie(url = url, alias = myAlias)
        }
    }

    @Test
    fun `rejects shortie with time in past`() = runTest {
        val clock = FakeClock()
        GlobalClockProvider.replaceClock(clock)
        val yesterday = LocalDateTime.nowInstant.minus(1.days).toLocalDateTime(TimeZone.UTC)
        assertFailsWith(ExpiryInPastError::class) {
            service.generateShortie(url = OriginalUrl("https://ex.com"), expiry = yesterday)
        }
    }

    @Test
    fun `deletes shorties, ignores unknowns`() = runTest {
        val links =
            listOf(
                "https://www.example.com",
                "https://www.example2.com",
                "https://www.example3.com",
                "https://www.example4.com",
            )
        val codes = links.map { service.generateShortie(OriginalUrl(it)) }

        val toDelete = codes.map { it.shortCode }.toMutableList()
        toDelete += listOf(ShortCode("not-know-1"), ShortCode("not-know-2"))

        assertEquals(4, service.getShorties(10)?.data?.size)
        assertEquals(4, service.removeShorties(toDelete))
        assertEquals(0, service.getShorties(10)?.data?.size)
    }
}

private class FakeGlobalCounter : GlobalCounter {
    private var id = 1L

    override suspend fun getNextId(): Long = ++id
}
