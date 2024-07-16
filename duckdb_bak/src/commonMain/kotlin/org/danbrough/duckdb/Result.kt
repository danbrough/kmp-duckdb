package org.danbrough.duckdb

expect interface ResultHandle : AutoCloseable


expect class Result : ResultHandle {

  val rowCount: Long

  val columnCount: Long

  val rowsChanged: Long

  override fun close()

  fun isNull(row: Long, col: Long): Boolean

  inline fun <reified T : Any?> get(row: Long, col: Long): T

}