package shortie.tests.data

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.data.clicks.ClicksDailyRepository
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.today
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import shortie.tests.DatabaseUtils
import shortie.tests.DatabaseUtils.clearDatabase
import shortie.tests.FakeClock

class ClicksDailyRepoTest {
    val db = DatabaseUtils.getTestDatabase()

    val clickRepo = ClicksDailyRepository(db)
    val urlRepo = UrlsRepository(db)

    init {
        DatabaseUtils.initDatabase()
    }

    @BeforeTest
    fun beforeEach() {
        clearDatabase(db)
        GlobalClockProvider.resetClock()
    }

    @Test
    fun `increments click count by selected amount`() = runTest {
        val clock = FakeClock()
        GlobalClockProvider.replaceClock(clock)

        val today = LocalDate.today
        val code = ShortCode("test")

        urlRepo.insert(url = OriginalUrl("https://test.com"), code = code)

        clickRepo.incrementCount(code, today)
        val result = clickRepo.getCount(code, today, today)
        assertEquals(1, result.size)
        val clickCount = result[today]
        assertEquals(1, clickCount)
    }
}
