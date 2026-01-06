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
