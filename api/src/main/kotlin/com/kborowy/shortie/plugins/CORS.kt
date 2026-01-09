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

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import org.slf4j.LoggerFactory

fun Application.configureCORS() {
    val log = LoggerFactory.getLogger("CORS plugin")
    val enabled = environment.config.property("ktor.allowCORS").getString().lowercase() == "true"
    if (enabled) {
        log.info("CORS plugin enabled")
        install(CORS) {
            anyMethod()
            allowHeader("Authorization")
            allowNonSimpleContentTypes = true
            anyHost()
        }
    } else {
        log.info("CORS plugin disabled")
    }
}
