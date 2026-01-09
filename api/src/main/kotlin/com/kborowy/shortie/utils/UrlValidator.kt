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

import org.apache.commons.validator.routines.UrlValidator as Validator

private val VALID_SCHEMES =
    listOf("http", "https", "ftp", "ftps", "sftp", "file", "data", "mailto", "tel", "ws", "wss")

object UrlValidator {
    fun validate(url: String): Boolean {
        val validator = Validator(VALID_SCHEMES.toTypedArray(), (Validator.ALLOW_LOCAL_URLS))
        return validator.isValid(url)
    }
}
