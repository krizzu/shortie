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
package com.kborowy.shortie.plugins

import com.github.mustachejava.DefaultMustacheFactory
import com.kborowy.shortie.models.ShortCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.mustache.Mustache

sealed class HtmlTemplates(val file: String) {
    object NotFound : HtmlTemplates(file = "404.html")

    object ServerError : HtmlTemplates("500.html")

    object Password : HtmlTemplates("password.html")

    object Home : HtmlTemplates("home.html")
}

fun HtmlTemplates.NotFound.model(page: String) = mapOf("page" to page)

fun HtmlTemplates.Password.model(shortCode: ShortCode) = mapOf("shortCode" to shortCode.value)

fun Application.configureTemplating() {
    install(Mustache) { mustacheFactory = DefaultMustacheFactory("templates") }
}
