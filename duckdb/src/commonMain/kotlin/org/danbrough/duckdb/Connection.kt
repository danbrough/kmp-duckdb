@file:OptIn(ExperimentalStdlibApi::class)

package org.danbrough.duckdb


expect interface ConnectionPeer : AutoCloseable

expect class Connection : ConnectionPeer {
  val database: Database

  fun query(sql: String): Result

  fun append(table: String): Appender

  fun prepareStatement(sql: String): PreparedStatement

  override fun close()
}

fun <R> Connection.query(sql: String, block: Result.() -> R) = query(sql).use(block)

fun Connection.append(table: String, block: Appender.() -> Unit) = append(table).use(block)

fun Connection.prepareStatement(sql: String, block: PreparedStatement.() -> Unit) =
  prepareStatement(sql).use(block)