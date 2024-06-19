package org.danbrough.duckdb

actual class PreparedStatement : NativePreparedStatement {
  actual val connection: Connection
    get() = TODO("Not yet implemented")

  actual override fun close() {
  }

}

actual interface NativePreparedStatement : AutoCloseable