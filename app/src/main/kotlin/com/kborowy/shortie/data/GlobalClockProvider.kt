@file:OptIn(ExperimentalTime::class)

package com.kborowy.shortie.data

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
}
