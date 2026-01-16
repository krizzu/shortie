package com.kborowy.shortie.data.clicks

import com.kborowy.shortie.extensions.now
import com.kborowy.shortie.models.ShortCode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert

interface ClicksDailyRepository {
    suspend fun incrementCount(code: ShortCode, date: LocalDate, by: Int = 1)

    suspend fun getCount(code: ShortCode, start: LocalDate, end: LocalDate): Map<LocalDate, Long>
}

fun ClicksDailyRepository(db: Database): ClicksDailyRepository = RealClicksDailyRepository(db)

private class RealClicksDailyRepository(private val db: Database) : ClicksDailyRepository {
    override suspend fun getCount(
        code: ShortCode,
        start: LocalDate,
        end: LocalDate,
    ): Map<LocalDate, Long> =
        transaction(db) {
            val result =
                ClicksDailyTable.selectAll()
                    .where { ClicksDailyTable.shortCode eq code.value }
                    .andWhere { ClicksDailyTable.clickDate greaterEq start }
                    .andWhere { ClicksDailyTable.clickDate lessEq end }
                    .toList()

            result.associate { it[ClicksDailyTable.clickDate] to it[ClicksDailyTable.clickCount] }
        }

    override suspend fun incrementCount(code: ShortCode, date: LocalDate, by: Int) {
        transaction(db) {
            ClicksDailyTable.upsert(
                ClicksDailyTable.shortCode,
                ClicksDailyTable.clickDate,
                onUpdate = {
                    it[ClicksDailyTable.clickCount] = ClicksDailyTable.clickCount + by.toLong()
                    it[ClicksDailyTable.updatedAt] = LocalDateTime.now
                },
            ) {
                it[shortCode] = code.value
                it[clickCount] = by.toLong() // initial insert
                it[clickDate] = date
            }
        }
    }
}
