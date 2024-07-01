package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_get_varchar
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_rows_changed
import org.danbrough.duckdb.cinterops.duckdb_value_boolean
import org.danbrough.duckdb.cinterops.duckdb_value_double
import org.danbrough.duckdb.cinterops.duckdb_value_float
import org.danbrough.duckdb.cinterops.duckdb_value_int16
import org.danbrough.duckdb.cinterops.duckdb_value_int32
import org.danbrough.duckdb.cinterops.duckdb_value_int64
import org.danbrough.duckdb.cinterops.duckdb_value_int8
import org.danbrough.duckdb.cinterops.duckdb_value_is_null
import org.danbrough.duckdb.cinterops.duckdb_value_uint16
import org.danbrough.duckdb.cinterops.duckdb_value_uint32
import org.danbrough.duckdb.cinterops.duckdb_value_uint64
import org.danbrough.duckdb.cinterops.duckdb_value_uint8
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.duckdb_value_varchar_internal

actual interface ResultHandle : NativePeer<duckdb_result>, AutoCloseable

actual class Result(val connection: Connection) : ResultHandle {
  override val handle: duckdb_result = nativeHeap.alloc<duckdb_result>()

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


  actual override fun close() {
    log.warn { "closing result" }
    duckdb_destroy_result(handle.ptr)
    nativeHeap.free(handle)
  }

  actual inline fun <reified T : Any?> get(row: ULong, col: ULong): T =
    when (T::class) {
      Boolean::class -> duckdb_value_boolean(handle.ptr, col, row)
      String::class -> duckdb_value_varchar(handle.ptr, col, row).let { data ->
        val s = data?.toKStringFromUtf8()
        duckdb_free(data)
        s
      }

      Byte::class -> duckdb_value_int8(handle.ptr, col, row)
      Short::class -> duckdb_value_int16(handle.ptr, col, row)
      Int::class -> duckdb_value_int32(handle.ptr, col, row)
      Long::class -> duckdb_value_int64(handle.ptr, col, row)
      UByte::class -> duckdb_value_uint8(handle.ptr, col, row)
      UShort::class -> duckdb_value_uint16(handle.ptr, col, row)
      UInt::class -> duckdb_value_uint32(handle.ptr, col, row)
      ULong::class -> duckdb_value_uint64(handle.ptr, col, row)
      Float::class -> duckdb_value_float(handle.ptr, col, row)
      Double::class -> duckdb_value_double(handle.ptr, col, row)
      else -> error("Invalid type: ${T::class}")
    } as T

  actual fun isNull(row: ULong, col: ULong): Boolean = duckdb_value_is_null(handle.ptr, col, row)


}

