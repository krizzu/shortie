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

data class ShortieAnalyticDetails(
    val shortCode: ShortCode,
    val totalClicks: Long,
    val lastClick: LocalDateTime?, // null means never clicked
    val clicksOverTime: Map<LocalDate, Long>,
)

data class ShortieAnalyticOverview(
    val totalClicks: Int,
    val totalLinks: Int,
    val activeLinks: Int,
    val expiredLinks: Int,
)
