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
package com.kborowy.shortie.services.analytics

import com.kborowy.shortie.data.clicks.ClicksDailyRepository
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.extensions.today
import com.kborowy.shortie.models.ShortieUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface AnalyticService {

    /** Increments total clicks and last redirected date */
    fun incrementClick(shortie: ShortieUrl)

    /** gathers analytics data for given shortie returns null if not found */
    suspend fun getDetails(
        shortie: ShortieUrl,
        start: LocalDate,
        end: LocalDate,
    ): ShortieAnalyticDetails?

    /** return the accumulated data about links in system */
    suspend fun totalOverview(): ShortieAnalyticOverview
}

fun AnalyticService(
    urlRepo: UrlsRepository,
    dailyRepo: ClicksDailyRepository,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    log: Logger = LoggerFactory.getLogger("AnalyticService"),
): AnalyticService = RealAnalyticService(urlRepo, dailyRepo, scope, log)

private class RealAnalyticService(
    private val urlRepo: UrlsRepository,
    private val dailyRepo: ClicksDailyRepository,
    private val scope: CoroutineScope,
    private val log: Logger,
) : AnalyticService {
    override fun incrementClick(shortie: ShortieUrl) {
        scope.launch(Dispatchers.IO) {
            coroutineScope {
                launch { urlRepo.incrementClickCount(shortie.shortCode) }
                launch { dailyRepo.incrementCount(shortie.shortCode, LocalDate.today) }
            }
            log.info("${shortie.shortCode} click bump")
        }
    }

    override suspend fun getDetails(
        shortie: ShortieUrl,
        start: LocalDate,
        end: LocalDate,
    ): ShortieAnalyticDetails? = coroutineScope {
        val totalResults = async { urlRepo.get(shortie.shortCode) }
        val detailsResults = async { dailyRepo.getDailyCount(shortie.shortCode, start, end) }

        val totals = totalResults.await() ?: return@coroutineScope null

        ShortieAnalyticDetails(
            shortCode = totals.shortCode,
            totalClicks = totals.totalClicks,
            lastClick = totals.lastRedirect,
            clicksOverTime = detailsResults.await(),
        )
    }

    override suspend fun totalOverview(): ShortieAnalyticOverview {
        val links = urlRepo.getLinksTotals(LocalDateTime.now)

        return ShortieAnalyticOverview(
            totalClicks = links.clicks.toInt(),
            totalLinks = links.total.toInt(),
            activeLinks = links.active.toInt(),
            expiredLinks = links.expired.toInt(),
        )
    }
}
