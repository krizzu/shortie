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
