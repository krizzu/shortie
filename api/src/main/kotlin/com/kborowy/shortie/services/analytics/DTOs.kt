package com.kborowy.shortie.services.analytics

import com.kborowy.shortie.models.ShortCode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class ShortieAnalyticDetails(
    val shortCode: ShortCode,
    val totalClicks: Long,
    val lastClick: LocalDateTime?, // null means never clicked
    val clicksOverTime: Map<LocalDate, Long>,
)

data class ShortieAnalyticWeeklyOverview(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalClicks: Int,
    val avgClicks: Float,
)

data class ShortieAnalyticOverview(
    val totalUrls: Int,
    val totalClicks: Int,
    val avgClickPerDay: Float,
)
