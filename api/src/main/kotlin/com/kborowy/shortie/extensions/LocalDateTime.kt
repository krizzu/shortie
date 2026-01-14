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

package com.kborowy.shortie.extensions

import com.kborowy.shortie.data.GlobalClockProvider
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/** Returns current date time in utc timezone */
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
