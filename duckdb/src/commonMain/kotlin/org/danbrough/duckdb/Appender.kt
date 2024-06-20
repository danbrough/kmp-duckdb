package org.danbrough.duckdb

expect interface NativeAppender : AutoCloseable

expect class Appender {
  val connection: Connection
  val table: String

  fun flush()

  inner class Row {
    fun appendInt8(i: Byte): Row
    fun appendInt16(i: Short): Row
    fun appendInt32(i: Int): Row
    fun appendInt64(i: Long): Row

    fun appendNull(): Row
    fun appendFloat(i: Float): Row
    fun appendDouble(i: Double): Row
    fun appendVarchar(i: String): Row
    fun appendBoolean(i: Boolean): Row
  }


  fun row(block: Row.() -> Unit)
}