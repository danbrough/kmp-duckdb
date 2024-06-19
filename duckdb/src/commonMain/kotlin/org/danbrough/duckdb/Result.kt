package org.danbrough.duckdb

expect interface ResultHandle: AutoCloseable

expect class Result : ResultHandle {


  val rowCount: ULong

  val columnCount: ULong

  val rowsChanged: ULong
  override fun close()

}