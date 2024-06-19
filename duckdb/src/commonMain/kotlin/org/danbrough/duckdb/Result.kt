package org.danbrough.duckdb

import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_query

expect interface ResultHandle: AutoCloseable

expect class Result : ResultHandle {


  val rowCount: ULong

  val columnCount: ULong

  val rowsChanged: ULong

}