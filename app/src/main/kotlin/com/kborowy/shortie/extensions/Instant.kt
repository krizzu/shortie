package com.kborowy.shortie.extensions

import com.kborowy.shortie.data.GlobalClockProvider
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val Instant.toLocalDateTimeUTC: LocalDateTime
    get() = toLocalDateTime(TimeZone.UTC)

val Instant.Companion.now: Instant
    get() = GlobalClockProvider.clock.now()
