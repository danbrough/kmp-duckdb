package org.danbrough.duckdb
actual interface ConnectionPeer : AutoCloseable

actual class Connection : ConnectionPeer {
  actual val database: Database
    get() = TODO("Not yet implemented")

  actual fun query(sql: String): Result {
    TODO("Not yet implemented")
  }

  actual fun append(table: String): Appender {
    TODO("Not yet implemented")
  }

  actual fun prepareStatement(sql: String): PreparedStatement {
    TODO("Not yet implemented")
  }

  actual override fun close() {
  }

}

