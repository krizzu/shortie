package com.kborowy.shortie.extensions

import com.kborowy.shortie.errors.NotFoundHttpError
import io.ktor.http.Parameters

fun Parameters.getOrFail(name: String): String {
    return this[name] ?: throw NotFoundHttpError("parameter $name not found")
}
