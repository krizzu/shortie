package com.kborowy.shortie

import com.kborowy.shortie.data.DataDIModule
import com.kborowy.shortie.errors.AppHttpError
import com.kborowy.shortie.infra.provideDatabaseDIModule
import com.kborowy.shortie.infra.runDatabaseMigrations
import com.kborowy.shortie.migrations.com.kborowy.shortie.utils.IdGenerator
import com.kborowy.shortie.routes.urls.configureUrlsRouting
import com.kborowy.shortie.services.ServicesDIModule
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.getAs
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    EngineMain.main(args)
}

// main scaffold module
fun Application.app() {
    setupDI()
    setupStatusPages()

    runDatabaseMigrations()

    configureUrlsRouting()
}

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

fun Application.setupStatusPages() {
    install(StatusPages) {
        exception<AppHttpError> { call, cause ->
            call.respondText(
                text = "${cause.statusCode}: ${cause.message}",
                status = cause.statusCode,
            )
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondText(
                "<h1>Page Not Found</h1>",
                ContentType.Text.Html,
                HttpStatusCode.NotFound,
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText("500: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }
    }
}
