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

  actual fun getVarchar(row: ULong, col: ULong): String {
    TODO("Not yet implemented")
  }

  actual fun getULong(row: ULong, col: ULong): ULong {
    TODO("Not yet implemented")
  }

  actual fun getUInt(row: ULong, col: ULong): UInt {
    TODO("Not yet implemented")
  }


}