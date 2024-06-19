package org.danbrough.duckdb

expect interface NativePreparedStatement : AutoCloseable
expect class PreparedStatement : NativePreparedStatement {
  val connection: Connection

  override fun close()

}