package org.danbrough.duckdb

actual class PreparedStatement : NativePreparedStatement {
  actual val connection: Connection
    get() = TODO("Not yet implemented")

  actual override fun close() {
  }

  actual val sql: String
    get() = TODO("Not yet implemented")

  actual fun clearBindings() {
  }

  actual inline fun <T : Any> bind(
    index: Int,
    value: T
  ): PreparedStatement {
    TODO("Not yet implemented")
  }

  actual inline fun bindNull(index: Int): PreparedStatement {
    TODO("Not yet implemented")
  }

}

actual interface NativePreparedStatement : AutoCloseable