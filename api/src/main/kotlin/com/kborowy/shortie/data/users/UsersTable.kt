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

import com.kborowy.shortie.extensions.now
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

private const val MAX_PASSWORD_LENGTH = 255
private const val MAX_USERNAME_LENGTH = 64
const val DEFAULT_ADMIN_NAME = "admin"

object UsersTable : LongIdTable("users") {
    val name = varchar("name", MAX_USERNAME_LENGTH).uniqueIndex()
    val password = varchar("password", MAX_PASSWORD_LENGTH)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now }
}
