/*
 * Copyright 2026 Krzysztof Borowy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
