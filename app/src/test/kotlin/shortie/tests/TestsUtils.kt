package shortie.tests

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
