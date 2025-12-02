package com.kborowy.shortie.data.urls

import com.kborowy.shortie.extensions.now
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

private const val MAX_SHORT_URL_LENGTH = 10
private const val MAX_LONG_URL_LENGTH = 2048
private const val MAX_PASSWORD_LENGTH = 128

object UrlsTable : Table(name = "urls") {
    val id = long("id").autoIncrement()
    val shortUrl = varchar("short_url", MAX_SHORT_URL_LENGTH).uniqueIndex()
    val originalUrl = varchar("original_url", MAX_LONG_URL_LENGTH)
    val passwordHash = varchar("password_hash", MAX_PASSWORD_LENGTH).nullable().default(null)
    val expiryDate = datetime("expiry_date").nullable().default(null)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now }

    override val primaryKey = PrimaryKey(id)
}
