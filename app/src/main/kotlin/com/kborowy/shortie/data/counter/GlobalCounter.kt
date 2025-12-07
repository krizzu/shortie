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
