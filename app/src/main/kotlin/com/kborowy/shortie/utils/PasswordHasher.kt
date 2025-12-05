package com.kborowy.shortie.utils

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper

object PasswordHasher {

    private val argon2 by lazy { Argon2Factory.create() }
    private val iterations by lazy { Argon2Helper.findIterations(argon2, 1000, 65536, 1) }

    fun hash(password: String): String {
        return argon2.hash(iterations, 65536, 1, password.toCharArray())
    }

    fun verify(password: String, hash: String): Boolean {
        return argon2.verify(hash, password.toCharArray())
    }
}
