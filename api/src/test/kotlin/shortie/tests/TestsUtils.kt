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
package shortie.tests

import com.kborowy.shortie.utils.ShortCodeGenerator
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

// 1st of December, 2025 at 00:00 UTC
private const val fakeClockStart = 1_764_547_200_000L

class FakeClock(val start: Long = fakeClockStart) : Clock {
    private var now: Long = start

    override fun now(): Instant {
        return Instant.fromEpochMilliseconds(now)
    }

    fun forward(duration: Duration) {
        now = now().plus(duration).toEpochMilliseconds()
    }

    fun backward(duration: Duration) {
        now = now().minus(duration).toEpochMilliseconds()
    }

    fun reset() {
        now = start
    }

    fun setTo(time: Instant) {
        now = time.toEpochMilliseconds()
    }
}

val FakeShortCodeGenerator =
    ShortCodeGenerator("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
