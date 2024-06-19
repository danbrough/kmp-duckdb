package org.danbrough.duckdb

expect interface NativeAppender : AutoCloseable

expect class Appender {
  val connection:Connection
  val table:String

  fun flush()

  inner class Row{

  }


  fun row(block: Row.() -> Unit)
}