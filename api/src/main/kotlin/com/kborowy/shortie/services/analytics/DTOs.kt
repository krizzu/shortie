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
