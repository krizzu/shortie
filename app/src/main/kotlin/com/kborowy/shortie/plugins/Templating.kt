package com.kborowy.shortie.plugins

import com.github.mustachejava.DefaultMustacheFactory
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

fun Application.configureTemplating() {
    install(Mustache) { mustacheFactory = DefaultMustacheFactory("templates") }
}
