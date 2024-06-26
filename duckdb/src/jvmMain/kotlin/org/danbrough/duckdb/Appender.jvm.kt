package org.danbrough.duckdb

actual interface NativeAppender : AutoCloseable

actual class Appender : NativeAppender {
  actual val connection: Connection
    get() = TODO("Not yet implemented")
  actual val table: String
    get() = TODO("Not yet implemented")

  actual fun flush() {
  }

  actual inner class Row {


    actual fun appendNull(): Row {
      TODO("Not yet implemented")
    }

    actual inline fun <T : Any> append(value: T): Row {
      TODO("Not yet implemented")
    }


  }

  actual fun row(block: Row.() -> Unit) {
  }

  actual override fun close() {
    TODO("Not yet implemented")
  }


}