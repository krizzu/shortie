package shortie.tests.utils

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.errors.TokenVerificationFailed
import com.kborowy.shortie.extensions.nowInstant
import com.kborowy.shortie.utils.JwtToken
import com.kborowy.shortie.utils.JwtVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.LocalDateTime
import shortie.tests.FakeClock

class JwtVerifierTests {

    private fun getVerifier(
        pass: String = "pass",
        aud: String = "aud",
        iss: String = "iss",
        realm: String = "real",
    ): JwtVerifier {
        return JwtVerifier(pass = pass, aud = aud, iss = iss, realm = realm)
    }

    @Test
    fun `validates expiry date correctly`() {
        val now = LocalDateTime.nowInstant
        val clock = FakeClock(now.toEpochMilliseconds())
        GlobalClockProvider.replaceClock(clock)

        val verifier = getVerifier()
        val token = verifier.issueNewToken(JwtToken.TokenType.Access, expiry = 5.minutes)
        assertEquals(Unit, verifier.verifyToken(token))

        clock.forward(4.minutes)
        assertEquals(Unit, verifier.verifyToken(token), "Should be still valid 1 minute before exp")

        clock.forward(6.minutes)
        assertFailsWith(TokenVerificationFailed::class) { verifier.verifyToken(token) }
    }
}
