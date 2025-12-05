package com.kborowy.shortie.migrations.com.kborowy.shortie.utils

import com.kborowy.shortie.models.ShortCode
import org.sqids.Sqids

interface IdGenerator {
    fun generateShortCode(id: Long): ShortCode
}

fun IdGenerator(alphabet: String, minLength: Int = 3): IdGenerator =
    RealIdGenerator(alphabet, minLength)

private class RealIdGenerator(alphabet: String, minLength: Int) : IdGenerator {

    private val sqids = Sqids(alphabet = alphabet, minLength = minLength)

    override fun generateShortCode(id: Long): ShortCode {
        val encoded = sqids.encode(listOf(id))
        return ShortCode(value = encoded)
    }
}
