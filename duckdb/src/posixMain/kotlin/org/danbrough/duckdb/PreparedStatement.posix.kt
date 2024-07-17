package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_bind_boolean
import org.danbrough.duckdb.cinterops.duckdb_bind_double
import org.danbrough.duckdb.cinterops.duckdb_bind_float
import org.danbrough.duckdb.cinterops.duckdb_bind_int16
import org.danbrough.duckdb.cinterops.duckdb_bind_int32
import org.danbrough.duckdb.cinterops.duckdb_bind_int64
import org.danbrough.duckdb.cinterops.duckdb_bind_int8
import org.danbrough.duckdb.cinterops.duckdb_bind_null
import org.danbrough.duckdb.cinterops.duckdb_bind_uint16
import org.danbrough.duckdb.cinterops.duckdb_bind_uint32
import org.danbrough.duckdb.cinterops.duckdb_bind_uint64
import org.danbrough.duckdb.cinterops.duckdb_bind_uint8
import org.danbrough.duckdb.cinterops.duckdb_bind_varchar
import org.danbrough.duckdb.cinterops.duckdb_clear_bindings
import org.danbrough.duckdb.cinterops.duckdb_destroy_prepare
import org.danbrough.duckdb.cinterops.duckdb_execute_prepared
import org.danbrough.duckdb.cinterops.duckdb_prepare
import org.danbrough.duckdb.cinterops.duckdb_prepare_error
import org.danbrough.duckdb.cinterops.duckdb_prepared_statementVar

actual interface NativePreparedStatement : AutoCloseable, NativePeer<duckdb_prepared_statementVar>

actual class PreparedStatement(
  actual val connection: Connection,
  actual val sql: String
) : NativePreparedStatement {

  override val handle: duckdb_prepared_statementVar = nativeHeap.alloc()

  init {
    duckdb_prepare(connection.handle.value, sql, handle.ptr).handleDuckDbError {
      "duckdb_prepare failed for $sql"
    }
  }

  actual override fun close() {
    duckdb_destroy_prepare(handle.ptr)
    nativeHeap.free(handle)
  }


  actual fun clearBindings() {
    if (duckdb_clear_bindings(handle.value) == DuckDBError)
      error("duckdb_clear_bindings failed")
  }

  private fun executeWithResult(): Result = Result(connection).also {
    if (duckdb_execute_prepared(handle.value, it.handle.ptr) == DuckDBError)
      error("duckdb_execute_prepared failed: ${prepareError()}")
  }

  actual fun <R> execute(block: Result.() -> R) =
    executeWithResult().use(block)

  private fun prepareError(): String? =
    duckdb_prepare_error(handle.value)?.toKString()

  actual inline fun <T : Any> bind(
    index: Int,
    value: T
  ): PreparedStatement {
    when (value) {
      is Byte -> duckdb_bind_int8(handle.value, index.toULong(), value)
      is Short -> duckdb_bind_int16(handle.value, index.toULong(), value)
      is Int -> duckdb_bind_int32(handle.value, index.toULong(), value)
      is Long -> duckdb_bind_int64(handle.value, index.toULong(), value)
      is UByte -> duckdb_bind_uint8(handle.value, index.toULong(), value)
      is UShort -> duckdb_bind_uint16(handle.value, index.toULong(), value)
      is UInt -> duckdb_bind_uint32(handle.value, index.toULong(), value)
      is ULong -> duckdb_bind_uint64(handle.value, index.toULong(), value)
      is Boolean -> duckdb_bind_boolean(handle.value, index.toULong(), value)
      is String -> duckdb_bind_varchar(handle.value, index.toULong(), value)
      is Float -> duckdb_bind_float(handle.value, index.toULong(), value)
      is Double -> duckdb_bind_double(handle.value, index.toULong(), value)
      else -> error("Unhandled bind value: $value")
    }.handleDuckDbError {
      "bind failed for index: $index value:$value"
    }
    return this
  }

  actual inline fun bindNull(index: Int): PreparedStatement {
    duckdb_bind_null(handle.value, index.toULong())
    return this
  }
}