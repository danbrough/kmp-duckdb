package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import org.danbrough.duckdb.cinterops.duckdb_connectionVar

expect interface NativeConnection : AutoCloseable

expect class Connection : NativeConnection {
  val database: Database

  fun query(sql: String): Result

  fun append(table: String): Appender

  fun prepareStatement(sql: String): PreparedStatement

}

fun <R> Connection.query(sql: String, block: Result.() -> R) = query(sql).use(block)


fun Connection.prepareStatement(sql: String, block: PreparedStatement.() -> Unit) =
  prepareStatement(sql).use(block)