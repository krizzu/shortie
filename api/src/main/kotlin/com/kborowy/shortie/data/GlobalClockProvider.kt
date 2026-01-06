@file:OptIn(ExperimentalTime::class)

package com.kborowy.shortie.data

import java.time.Instant
import java.time.ZoneId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

/** Global access to a Clock provider To help with tests */
object GlobalClockProvider {

    private var _clock: Clock = Clock.System

    val clock: Clock
        get() = _clock

    internal fun replaceClock(clock: Clock) {
        _clock = clock
    }

    internal fun resetClock() {
        _clock = Clock.System
    }

    val javaClock: java.time.Clock
        get() {
            return object : java.time.Clock() {
                override fun getZone(): ZoneId = systemDefaultZone().zone

                override fun withZone(zone: ZoneId): java.time.Clock = system(zone)

                override fun instant(): Instant = clock.now().toJavaInstant()
            }
        }
}
