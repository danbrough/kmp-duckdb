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
    actual fun appendInt32(i: Int): Row {
      TODO("Not yet implemented")
    }

    actual fun appendInt8(i: Byte): Row {
      TODO("Not yet implemented")
    }

    actual fun appendInt16(i: Short): Row {
      TODO("Not yet implemented")
    }

    actual fun appendInt64(i: Long): Row {
      TODO("Not yet implemented")
    }

    actual fun appendNull(): Row {
      TODO("Not yet implemented")
    }

    actual fun appendFloat(i: Float): Row {
      TODO("Not yet implemented")
    }

    actual fun appendDouble(i: Double): Row {
      TODO("Not yet implemented")
    }

    actual fun appendVarchar(i: String): Row {
      TODO("Not yet implemented")
    }

    actual fun appendBoolean(i: Boolean): Row {
      TODO("Not yet implemented")
    }

  }

  actual fun row(block: Row.() -> Unit) {
  }


}