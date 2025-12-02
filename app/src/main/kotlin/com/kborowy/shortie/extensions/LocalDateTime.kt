@file:OptIn(ExperimentalTime::class)

package com.kborowy.shortie.extensions

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Returns current date in utc timezone
 */
val LocalDateTime.Companion.now: LocalDateTime
    get() = Clock.System.now().toLocalDateTime(TimeZone.UTC)
