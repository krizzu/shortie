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
import com.kborowy.shortie.data.clicks.ClicksDailyRepository
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.today
import com.kborowy.shortie.models.OriginalUrl
import com.kborowy.shortie.models.ShortCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
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
        clickRepo.incrementCount(code, today)
        clickRepo.incrementCount(code, today)
        var result = clickRepo.getShortieDetailsForDuration(code, today, today)
        assertEquals(1, result.size)
        assertEquals(3, result[today])
        clickRepo.incrementCount(code, today, 5)
        result = clickRepo.getShortieDetailsForDuration(code, today, today)
        assertEquals(8, result[today])
    }

    @Test
    fun `gets counts for correct date ranges`() = runTest {
        val clock = FakeClock()
        GlobalClockProvider.replaceClock(clock)

        val day1 = LocalDate.today
        val day2 = day1 + DatePeriod(days = 2)
        val day3 = day1 + DatePeriod(days = 3)
        val day4 = day1 + DatePeriod(days = 4)

        val code = ShortCode("test")
        urlRepo.insert(url = OriginalUrl("https://test1.com"), code = code)

        clickRepo.incrementCount(code, day1, 100)
        clickRepo.incrementCount(code, day2, 20)
        clickRepo.incrementCount(code, day3, 30)
        clickRepo.incrementCount(code, day4, 125)

        var result = clickRepo.getShortieDetailsForDuration(code, start = day1, end = day2)
        assertEquals(2, result.size)
        assertEquals(100, result[day1])
        assertEquals(20, result[day2])

        result = clickRepo.getShortieDetailsForDuration(code, start = day2, end = day4)
        assertEquals(3, result.size)
        assertEquals(175, result.toList().sumOf { it.second })
    }
}
