@file:OptIn(ExperimentalTime::class)

package com.kborowy.shortie.extensions

import com.kborowy.shortie.data.GlobalClockProvider
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/** Returns current date in utc timezone */
val LocalDateTime.Companion.now: LocalDateTime
    get() = GlobalClockProvider.clock.now().toLocalDateTime(TimeZone.UTC)

/** Returns current time and date as instant in utc timezone */
val LocalDateTime.Companion.nowInstant
    get() = this.now.toInstant(UtcOffset.ZERO)

@OptIn(ExperimentalTime::class)
val LocalDateTime.isInPast: Boolean
    get() {
        return this.toInstant(UtcOffset.ZERO) <= LocalDateTime.now.toInstant(UtcOffset.ZERO)
    }


val LocalDateTime.asInstantUTC
    get() = toInstant(TimeZone.UTC)