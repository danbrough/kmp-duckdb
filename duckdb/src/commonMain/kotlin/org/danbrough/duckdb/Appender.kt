package org.danbrough.duckdb

expect interface NativeAppender : AutoCloseable

expect class Appender : NativeAppender {
  val connection: Connection
  val table: String

  fun flush()

  inner class Row {

    fun appendNull(): Row

    inline fun <T : Any> append(value: T): Row
  }

  override fun close()

  fun row(block: Row.() -> Unit)
}