@file:OptIn(ExperimentalTime::class)

package tests.tests.services

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.extensions.nowInstant
import com.kborowy.shortie.migrations.com.kborowy.shortie.data.counter.GlobalCounter
import com.kborowy.shortie.migrations.com.kborowy.shortie.utils.IdGenerator
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.services.urls.UrlsService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import tests.tests.DatabaseUtils
import tests.tests.DatabaseUtils.clearDatabase

class RealUrlsServiceTest {

    val db = DatabaseUtils.getTestDatabase()
    val repo = UrlsRepository(db)
    val service =
        UrlsService(
            repo = repo,
            counter = FakeGlobalCounter(),
            generator =
                IdGenerator("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"),
        )

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

    fun `generates shortie with password`() = runTest {
        val url = OriginalUrl("https://www.example3.com")
        val password = "my_secret"
        val shortie = service.generateShortie(url = url, password = password)
        service.resolveShortCode()


    }
}

private class FakeGlobalCounter : GlobalCounter {
    private var id = 1L

    override suspend fun getNextId(): Long = ++id
}

// 1st of December, 2025 at 00:00 UTC
private const val fakeClockStart = 1_764_547_200_000L

private class FakeClock : Clock {

    var now: Long = fakeClockStart

    override fun now(): Instant {
        return Instant.fromEpochMilliseconds(now)
    }

    fun forward(duration: Duration) {
        now = now().plus(duration).toEpochMilliseconds()
    }

    fun backward(duration: Duration) {
        now = now().minus(duration).toEpochMilliseconds()
    }

    fun reset() {
        now = fakeClockStart
    }
}
