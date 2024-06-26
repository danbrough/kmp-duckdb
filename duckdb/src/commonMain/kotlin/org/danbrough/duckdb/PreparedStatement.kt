package org.danbrough.duckdb

import org.danbrough.duckdb.Appender.Row

expect interface NativePreparedStatement : AutoCloseable
expect class PreparedStatement : NativePreparedStatement {
  val connection: Connection
  val sql: String

  inline fun bindNull(index:Int): PreparedStatement

  inline fun <T : Any> bind(index: Int, value: T): PreparedStatement

  fun clearBindings()

  override fun close()

}