package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_bind_boolean
import org.danbrough.duckdb.cinterops.duckdb_bind_int16
import org.danbrough.duckdb.cinterops.duckdb_bind_int32
import org.danbrough.duckdb.cinterops.duckdb_bind_int64
import org.danbrough.duckdb.cinterops.duckdb_bind_int8
import org.danbrough.duckdb.cinterops.duckdb_bind_varchar
import org.danbrough.duckdb.cinterops.duckdb_clear_bindings
import org.danbrough.duckdb.cinterops.duckdb_destroy_prepare
import org.danbrough.duckdb.cinterops.duckdb_execute_prepared
import org.danbrough.duckdb.cinterops.duckdb_prepare
import org.danbrough.duckdb.cinterops.duckdb_prepare_error
import org.danbrough.duckdb.cinterops.duckdb_prepared_statementVar
import org.danbrough.duckdb.cinterops.idx_t

actual interface NativePreparedStatement : AutoCloseable, NativeObject<duckdb_prepared_statementVar>

actual class PreparedStatement(actual val connection: Connection,override val handle: duckdb_prepared_statementVar) :
  NativePreparedStatement {

  constructor(connection: Connection, sql: String, handle: duckdb_prepared_statementVar) : this(connection,handle) {

    if (duckdb_prepare(connection.handle.value, sql, handle.ptr) == DuckDBError)
      error("PreparedStatement::prepare failed. SQL:$sql. message: ${duckdb_prepare_error(handle.value)?.toKString()}")
  }


  override fun close() {
    duckdb_destroy_prepare(handle.ptr)
  }


  inline fun bindInt8(index: idx_t, value: Byte) =
    if (duckdb_bind_int8(handle.value, index, value) == DuckDBError)
      error("duckdb_bind_int8 failed")
    else Unit

  inline fun bindInt16(index: idx_t, value: Short) =
    if (duckdb_bind_int16(handle.value, index, value) == DuckDBError)
      error("duckdb_bind_int16 failed")
    else Unit

  inline fun bindInt32(index: idx_t, value: Int) =
    if (duckdb_bind_int32(handle.value, index, value) == DuckDBError)
      error("duckdb_bind_int32 failed: ${prepareError()}")
    else Unit

  inline fun bindInt64(index: idx_t, value: Long) =
    if (duckdb_bind_int64(handle.value, index, value) == DuckDBError)
      error("duckdb_bind_int64 failed")
    else Unit

  inline fun bindBoolean(index: idx_t, value: Boolean) =
    if (duckdb_bind_boolean(handle.value, index, value) == DuckDBError)
      error("duckdb_bind_boolean failed")
    else Unit

  inline fun bindVarchar(index: idx_t, value: String) =
    if (duckdb_bind_varchar(handle.value, index, value) == DuckDBError)
      error("duckdb_bind_varchar failed")
    else Unit

  fun clearBindings() {
    if (duckdb_clear_bindings(handle.value) == DuckDBError)
      error("duckdb_clear_bindings failed")
  }

  fun executeWithResult():Result = Result(connection).also {
    if (duckdb_execute_prepared(handle.value, it.handle.ptr) == DuckDBError)
      error("duckdb_execute_prepared failed: ${prepareError()}")
  }

  fun <R> executeWithResult(block: Result.() -> R) =
    executeWithResult().use(block)

  fun execute() {
    if (duckdb_execute_prepared(handle.value, null) == DuckDBError)
      error("duckdb_execute_prepared failed: ${prepareError()}")
  }


  fun prepareError(): String? =
    duckdb_prepare_error(handle.value)?.toKString()



}