package com.kborowy.shortie.data.clicks

import kotlinx.datetime.LocalDate

data class ClickDetails(val totalClicks: Long, val clicksByDate: Map<LocalDate, Long>)
