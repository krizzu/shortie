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
package com.kborowy.shortie.data.counter

import java.sql.Connection
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * todo:
 * - for super fast and atomic counter, use redis
 * - otherwise fallback to using postgres
 */
interface GlobalCounter {
    suspend fun getNextId(): Long
}

fun GlobalCounter(db: Database): GlobalCounter = RealGlobalCounter(db = db)

private class RealGlobalCounter(private val db: Database) : GlobalCounter {
    // this has be created in db first
    private val seqName = "global_counter_seq"

    override suspend fun getNextId(): Long = transaction(db) { readNextSequence(seqName) }
}

private fun JdbcTransaction.readNextSequence(name: String): Long {
    val cnx = connection.connection as Connection
    val query = cnx.createStatement()
    val result = query.executeQuery("SELECT nextval('$name');")
    result.next()
    return result.getLong(1)
}
