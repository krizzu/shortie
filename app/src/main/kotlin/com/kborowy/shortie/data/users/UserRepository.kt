package com.kborowy.shortie.data.users

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

interface UserRepository {
    suspend fun getAdminPassword(): String
}

fun UserRepository(db: Database): UserRepository = RealUserRepository(db)

private class RealUserRepository(private val db: Database) : UserRepository {
    override suspend fun getAdminPassword(): String =
        transaction(db) {
            UsersTable.select(UsersTable.password)
                .where { UsersTable.name eq DEFAULT_ADMIN_NAME }
                .single()[UsersTable.password]
        }
}
