package org.danbrough.duckdb

import kotlinx.cinterop.CValue
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_append_date
import org.danbrough.duckdb.cinterops.duckdb_append_double
import org.danbrough.duckdb.cinterops.duckdb_append_float
import org.danbrough.duckdb.cinterops.duckdb_append_int16
import org.danbrough.duckdb.cinterops.duckdb_append_int32
import org.danbrough.duckdb.cinterops.duckdb_append_int64
import org.danbrough.duckdb.cinterops.duckdb_append_int8
import org.danbrough.duckdb.cinterops.duckdb_append_null
import org.danbrough.duckdb.cinterops.duckdb_append_uint16
import org.danbrough.duckdb.cinterops.duckdb_append_uint32
import org.danbrough.duckdb.cinterops.duckdb_append_uint64
import org.danbrough.duckdb.cinterops.duckdb_append_uint8
import org.danbrough.duckdb.cinterops.duckdb_append_varchar
import org.danbrough.duckdb.cinterops.duckdb_appenderVar
import org.danbrough.duckdb.cinterops.duckdb_appender_close
import org.danbrough.duckdb.cinterops.duckdb_appender_create
import org.danbrough.duckdb.cinterops.duckdb_appender_destroy
import org.danbrough.duckdb.cinterops.duckdb_appender_end_row
import org.danbrough.duckdb.cinterops.duckdb_appender_error
import org.danbrough.duckdb.cinterops.duckdb_appender_flush
import org.danbrough.duckdb.cinterops.duckdb_date

actual interface NativeAppender : NativePeer<duckdb_appenderVar>, AutoCloseable

actual class Appender(
  actual val connection: Connection,
  actual val table: String,
) : NativeAppender {
  override val handle: duckdb_appenderVar = nativeHeap.alloc()

  init {
    duckdb_appender_create(connection.handle.value, null, table, handle.ptr).handleDuckDbError {
      "duckdb_appender_create table:$table failed"
    }
  }

  inline fun UInt.handleAppendError(msg: () -> String) =
    if (this == DuckDBError) {
      error("${msg()}: ${duckdb_appender_error(handle.value)?.toKString()}")
    } else Unit

  actual inner class Row {

    actual inline fun appendNull(): Row {
      duckdb_append_null(handle.value).handleAppendError {
        "duckdb_append_null failed"
      }
      return this
    }

    fun appendDate(i: CValue<duckdb_date>): Row {
      duckdb_append_date(handle.value, i).handleAppendError {
        "duckdb_append_double failed"
      }
      return this
    }

    actual inline fun <T : Any> append(value: T): Row {
      when (value) {

        is String -> duckdb_append_varchar(handle.value, value)
        is Byte -> duckdb_append_int8(handle.value, value)
        is Short -> duckdb_append_int16(handle.value, value)
        is Int -> duckdb_append_int32(handle.value, value)
        is Long -> duckdb_append_int64(handle.value, value)
        is UByte -> duckdb_append_uint8(handle.value, value)
        is UShort -> duckdb_append_uint16(handle.value, value)
        is UInt -> duckdb_append_uint32(handle.value, value)
        is ULong -> duckdb_append_uint64(handle.value, value)
        is Float -> duckdb_append_float(handle.value, value)
        is Double -> duckdb_append_double(handle.value, value)
        else -> error("invalid value: $value")

      }.handleAppendError { "duckdb_append $value failed" }
      return this
    }
  }


  actual override fun close() {
    if (duckdb_appender_close(handle.value) == DuckDBError)
      error("Appender::close(): failed ${duckdb_appender_error(handle.value)?.toKString()}")

    duckdb_appender_destroy(handle.ptr)
    nativeHeap.free(handle)
  }

  actual fun flush() {
    if (duckdb_appender_flush(handle.value) == DuckDBError)
      error("Appender::flush() failed: ${duckdb_appender_error(handle.value)?.toKString()}")
  }


  actual fun row(block: org.danbrough.duckdb.Appender.Row.() -> Unit) {
    Row().block()
    duckdb_appender_end_row(handle.value).handleAppendError { "duckdb_appender_end_row failed" }
  }
}