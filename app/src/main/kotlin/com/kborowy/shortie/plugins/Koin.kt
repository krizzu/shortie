package com.kborowy.shortie.plugins

import com.kborowy.shortie.data.DataDIModule
import com.kborowy.shortie.infra.provideDatabaseDIModule
import com.kborowy.shortie.migrations.com.kborowy.shortie.utils.IdGenerator
import com.kborowy.shortie.services.ServicesDIModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.getAs
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.setupDI() {
    val appModule = module {
        single<IdGenerator> {
            IdGenerator(
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
