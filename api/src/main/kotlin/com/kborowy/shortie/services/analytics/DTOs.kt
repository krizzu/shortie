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

import com.kborowy.shortie.models.ShortCode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class PaginatedShortieAnalyticLink(
    val hasNext: Boolean,
    val nextPage: Int?,
    val links: List<ShortieAnalyticLink>,
)

data class ShortieAnalyticLink(
    val shortCode: ShortCode,
    val totalClicks: Long,
    val lastClick: LocalDateTime?, // null means never clicked
)

/** used for generic list of analytic details */
data class AnalyticPeriodSummary(
    val totalClicksInPeriod: Long,
    val clicksPerDay: Map<LocalDate, Long>,
)

/** Analytic details for particular shortie */
data class ShortieAnalyticDetails(
    val shortie: ShortieAnalyticLink,
    val details: AnalyticPeriodSummary,
)

data class AnalyticTotalOverview(
    val totalClicks: Int,
    val totalLinks: Int,
    val activeLinks: Int,
    val expiredLinks: Int,
)
