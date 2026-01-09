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
