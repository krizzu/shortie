package shortie.tests.utils

import com.kborowy.shortie.data.GlobalClockProvider
import com.kborowy.shortie.errors.TokenError
import com.kborowy.shortie.errors.TokenExpiredError
import com.kborowy.shortie.extensions.nowInstant
import com.kborowy.shortie.utils.JwtToken
import com.kborowy.shortie.utils.JwtVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        assertNotNull(verifier.verifyToken(token))

        clock.forward(4.minutes)
        assertNotNull(verifier.verifyToken(token), "Should be still valid 1 minute before exp")

        clock.forward(6.minutes)
        assertFailsWith(TokenExpiredError::class) { verifier.verifyToken(token) }
    }

    @Test
    fun `correctly detects invalid token`() {
        val verifier = getVerifier()
        val verifierWrong = getVerifier(aud = "aud2")
        val verifierWrong2 = getVerifier(iss = "diff")
        val verifierWrong3 = getVerifier(pass = "wrong")
        val tokenWrong = verifierWrong.issueNewToken(JwtToken.TokenType.Access, expiry = 5.minutes)
        val tokenWrong2 =
            verifierWrong2.issueNewToken(JwtToken.TokenType.Access, expiry = 5.minutes)
        val tokenWrong3 =
            verifierWrong3.issueNewToken(JwtToken.TokenType.Access, expiry = 5.minutes)

        val err1 = assertFailsWith(TokenError::class) { verifier.verifyToken(tokenWrong) }
        assertEquals("The Claim 'aud' value doesn't contain the required audience.", err1.message)
        val err2 = assertFailsWith(TokenError::class) { verifier.verifyToken(tokenWrong2) }
        assertEquals("The Claim 'iss' value doesn't match the required issuer.", err2.message)
        val err3 = assertFailsWith(TokenError::class) { verifier.verifyToken(tokenWrong3) }
        assertEquals(
            "The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA256",
            err3.message,
        )
    }

    @Test
    fun `correctly detect wrong token type`() {
        val verifier = getVerifier()

        val token = verifier.issueNewToken(type = JwtToken.TokenType.Access, expiry = 1.minutes)

        val payload = verifier.verifyToken(token)
        assertNotNull(
            verifier.validateCredentials(payload, type = JwtToken.TokenType.Access),
            "should have validated access token",
        )
        assertNull(
            verifier.validateCredentials(payload, type = JwtToken.TokenType.Refresh),
            "access token validated as refresh token",
        )
    }
}
