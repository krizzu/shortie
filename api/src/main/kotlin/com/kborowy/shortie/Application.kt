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
package com.kborowy.shortie

import com.kborowy.shortie.infra.runDatabaseMigrations
import com.kborowy.shortie.plugins.configureAuth
import com.kborowy.shortie.plugins.configureCORS
import com.kborowy.shortie.plugins.configureCallLogging
import com.kborowy.shortie.plugins.configureContentNegotiation
import com.kborowy.shortie.plugins.configureDI
import com.kborowy.shortie.plugins.configureStatusPages
import com.kborowy.shortie.plugins.configureTemplating
import com.kborowy.shortie.routes.auth.authRouting
import com.kborowy.shortie.routes.redirect.redirectRouting
import com.kborowy.shortie.routes.staticContentRouting
import com.kborowy.shortie.routes.urls.urlsRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

// main scaffold module
fun Application.app() {
    configureDI()
    configureAuth()
    configureCORS()
    configureStatusPages()
    configureContentNegotiation()
    configureTemplating()
    configureCallLogging()

    runDatabaseMigrations()

    staticContentRouting()
    urlsRouting()
    redirectRouting()
    authRouting()
}
