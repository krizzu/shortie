package tests.tests.utils

import com.kborowy.shortie.utils.PasswordHasher
import kotlin.test.Test
import kotlin.test.assertTrue

class PasswordHasherTests {

    @Test
    fun `creates and verifies correct password`() {
        val password = "my very long password?"
        val hash = PasswordHasher.hash(password)
        assertTrue(PasswordHasher.verify(password, hash))
    }
}
