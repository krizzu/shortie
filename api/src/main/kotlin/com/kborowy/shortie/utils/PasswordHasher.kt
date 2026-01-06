package com.kborowy.shortie.utils

import de.mkammerer.argon2.Argon2Factory

object PasswordHasher {

    private const val ITERATIONS = 3
    private const val MEMORY = 65536
    private const val PARALLELISM = 1
    private val argon2 by lazy { Argon2Factory.create() }

    fun hash(password: String): String {
        return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray())
    }

    fun verify(password: String, hash: String): Boolean {
        return argon2.verify(hash, password.toCharArray())
    }
}
