package com.kborowy.shortie.data.clicks

import com.kborowy.shortie.data.urls.UrlsTable
import com.kborowy.shortie.extensions.now
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime

object ClicksDailyTable : LongIdTable("clicks_daily") {
    val shortCode =
        reference(
            "short_code",
            UrlsTable.shortCode,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE,
        )
    val clickDate = date("click_date")
    val clickCount = long("click_count").check { it greaterEq 0 }
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now }

    init {
        index(
            customIndexName = "clicks_short_code_date_unique",
            isUnique = true,
            shortCode,
            clickDate,
        )
    }
}
