package org.danbrough.duckdb

actual interface ResultHandle : AutoCloseable
actual class Result : ResultHandle {
  actual val rowCount: ULong
    get() = TODO("Not yet implemented")
  actual val columnCount: ULong
    get() = TODO("Not yet implemented")
  actual val rowsChanged: ULong
    get() = TODO("Not yet implemented")

  actual override fun close() {
  }


}