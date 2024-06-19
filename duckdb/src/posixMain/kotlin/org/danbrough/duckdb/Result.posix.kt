package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_rows_changed

actual interface ResultHandle : NativePeer<duckdb_result>, AutoCloseable

actual class Result(val connection: Connection) : ResultHandle {
  override val handle: duckdb_result = connection.database.scope.alloc<duckdb_result>().also {

  }

  constructor(connection: Connection, sql: String) : this(connection) {
    duckdb_query(connection.handle.value, sql, handle.ptr).handleDuckDbError {
      "query: $sql"
    }
  }
  /*
    constructor(conn: Connection2, handle: duckdb_result, sql: String) : this(handle) {

    }
  */


  actual val rowCount: ULong by lazy {
    duckdb_row_count(handle.ptr)
  }

  actual val columnCount: ULong by lazy {
    duckdb_column_count(handle.ptr)
  }

  actual val rowsChanged: ULong by lazy {
    duckdb_rows_changed(handle.ptr)
  }


  actual override fun close() = duckdb_destroy_result(handle.ptr)
}

