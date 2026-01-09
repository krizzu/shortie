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
