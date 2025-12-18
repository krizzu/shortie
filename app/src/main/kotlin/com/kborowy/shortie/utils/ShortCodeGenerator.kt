package com.kborowy.shortie.utils

import com.kborowy.shortie.models.ShortCode
import org.sqids.Sqids

interface ShortCodeGenerator {
    fun generateShortCode(id: Long): ShortCode

    fun decodeShortCode(code: ShortCode): Long?
}

fun ShortCodeGenerator(alphabet: String, minLength: Int = 3): ShortCodeGenerator =
    RealShortCodeGenerator(alphabet, minLength)

private class RealShortCodeGenerator(alphabet: String, minLength: Int) : ShortCodeGenerator {

    private val sqids = Sqids(alphabet = alphabet, minLength = minLength)

    override fun generateShortCode(id: Long): ShortCode {
        val encoded = sqids.encode(listOf(id))
        return ShortCode(value = encoded)
    }

    override fun decodeShortCode(code: ShortCode): Long? {
        val values = sqids.decode(code.value)
        if (values.size == 1) {
            return values.first()
        }
        return null
    }
}
