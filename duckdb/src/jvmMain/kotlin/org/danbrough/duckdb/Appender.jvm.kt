package org.danbrough.duckdb

actual interface NativeAppender : AutoCloseable
actual class Appender {
  actual val connection: Connection
    get() = TODO("Not yet implemented")
  actual val table: String
    get() = TODO("Not yet implemented")

  actual fun flush() {
  }

  actual inner class Row {

  }

  actual fun row(block: Row.() -> Unit) {
  }


}