package org.danbrough.duckdb

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
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

actual interface ResultHandle : NativePeer<duckdb_result>, AutoCloseable

actual class Result(val connection: Connection) : ResultHandle {
  override val handle: duckdb_result = nativeHeap.alloc<duckdb_result>()

  constructor(connection: Connection, sql: String) : this(connection) {
    duckdb_query(connection.handle.value, sql, handle.ptr).handleDuckDbError {
      "query: $sql"
    }
  }


  actual val rowCount: Long
    get() = duckdb_row_count(handle.ptr).toLong()

  actual val columnCount: Long
    get() = duckdb_column_count(handle.ptr).toLong()


  actual val rowsChanged: Long
    get() = duckdb_rows_changed(handle.ptr).toLong()


  actual override fun close() {
    duckdb_destroy_result(handle.ptr)
    nativeHeap.free(handle)
  }

  actual inline fun <reified T : Any?> get(row: Long, col: Long): T =
    when (T::class) {
      Boolean::class -> duckdb_value_boolean(handle.ptr, col.toULong(), row.toULong())
      String::class -> duckdb_value_varchar(handle.ptr, col.toULong(), row.toULong()).let { data ->
        val str = data?.toKStringFromUtf8()
        duckdb_free(data)
        str
      }

      Byte::class -> duckdb_value_int8(handle.ptr, col.toULong(), row.toULong())
      Short::class -> duckdb_value_int16(handle.ptr, col.toULong(), row.toULong())
      Int::class -> duckdb_value_int32(handle.ptr, col.toULong(), row.toULong())
      Long::class -> duckdb_value_int64(handle.ptr, col.toULong(), row.toULong())
      UByte::class -> duckdb_value_uint8(handle.ptr, col.toULong(), row.toULong())
      UShort::class -> duckdb_value_uint16(handle.ptr, col.toULong(), row.toULong())
      UInt::class -> duckdb_value_uint32(handle.ptr, col.toULong(), row.toULong())
      ULong::class -> duckdb_value_uint64(handle.ptr, col.toULong(), row.toULong())
      Float::class -> duckdb_value_float(handle.ptr, col.toULong(), row.toULong())
      Double::class -> duckdb_value_double(handle.ptr, col.toULong(), row.toULong())
      else -> error("Invalid type: ${T::class}")
    } as T

  actual fun isNull(row: Long, col: Long): Boolean =
    duckdb_value_is_null(handle.ptr, col.toULong(), row.toULong())


}

