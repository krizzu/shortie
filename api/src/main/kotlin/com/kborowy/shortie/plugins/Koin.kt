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

import com.kborowy.shortie.data.DataDIModule
import com.kborowy.shortie.infra.provideDatabaseDIModule
import com.kborowy.shortie.services.ServicesDIModule
import com.kborowy.shortie.utils.JwtVerifier
import com.kborowy.shortie.utils.ShortCodeGenerator
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.getAs
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    val appModule = module {
        single<Int?>(qualifier = named("proxy_port")) {
            environment.config.propertyOrNull("ktor.deployment.proxyPort")?.getString()?.toInt()
        }

        single<Json> {
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        }

        single<JwtVerifier> {
            JwtVerifier(
                pass = environment.config.property("auth.secret").getString(),
                aud = environment.config.property("auth.audience").getString(),
                iss = environment.config.property("auth.issuer").getString(),
                realm = environment.config.property("auth.realm").getString(),
            )
        }

        single<ShortCodeGenerator> {
            ShortCodeGenerator(
                alphabet = environment.config.property("hash.alphabet").getString(),
                minLength = environment.config.property("hash.minLength").getAs(),
            )
        }
    }

    install(Koin) {
        slf4jLogger()

        modules(appModule, provideDatabaseDIModule(), DataDIModule, ServicesDIModule)
    }
}
