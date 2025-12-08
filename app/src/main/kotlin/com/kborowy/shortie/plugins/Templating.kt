package com.kborowy.shortie.plugins

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.mustache.Mustache

enum class HtmlTemplates(val file: String) {
    NotFound("404.html"),
    Password("password.html"),
}

fun Application.configureTemplating() {
    install(Mustache) { mustacheFactory = DefaultMustacheFactory("templates") }
}
