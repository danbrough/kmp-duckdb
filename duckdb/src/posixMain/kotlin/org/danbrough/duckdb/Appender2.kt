package org.danbrough.duckdb

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_append_bool
import org.danbrough.duckdb.cinterops.duckdb_append_date
import org.danbrough.duckdb.cinterops.duckdb_append_double
import org.danbrough.duckdb.cinterops.duckdb_append_float
import org.danbrough.duckdb.cinterops.duckdb_append_int16
import org.danbrough.duckdb.cinterops.duckdb_append_int32
import org.danbrough.duckdb.cinterops.duckdb_append_int64
import org.danbrough.duckdb.cinterops.duckdb_append_int8
import org.danbrough.duckdb.cinterops.duckdb_append_null
import org.danbrough.duckdb.cinterops.duckdb_append_varchar
import org.danbrough.duckdb.cinterops.duckdb_appenderVar
import org.danbrough.duckdb.cinterops.duckdb_appender_close
import org.danbrough.duckdb.cinterops.duckdb_appender_create
import org.danbrough.duckdb.cinterops.duckdb_appender_destroy
import org.danbrough.duckdb.cinterops.duckdb_appender_end_row
import org.danbrough.duckdb.cinterops.duckdb_appender_error
import org.danbrough.duckdb.cinterops.duckdb_appender_flush
import org.danbrough.duckdb.cinterops.duckdb_date

@Suppress("MemberVisibilityCanBePrivate")
class Appender2(conn: Connection2, val table: String, override val handle: duckdb_appenderVar) :
	NativeObject<duckdb_appenderVar> {

	inline fun UInt.handleAppendError(msg: () -> String) =
		if (this == DuckDBError) {
			error("${msg()}: ${duckdb_appender_error(handle.value)?.toKString()}")
		} else Unit


	init {
		duckdb_appender_create(conn.handle.value, null, table, handle.ptr).handleDuckDbError {
			"duckdb_appender_create table:$table failed"
		}
	}

	inner class Row {


		inline fun appendInt32(i: Int) = apply {
			duckdb_append_int32(handle.value, i).handleAppendError { "duckdb_append_int32" }
		}

		inline fun appendInt64(i: Long) = apply {
			duckdb_append_int64(handle.value, i).handleAppendError {
				"duckdb_append_int64 failed"
			}
		}

		inline fun appendInt16(i: Short) = apply {
			duckdb_append_int16(handle.value, i).handleAppendError {
				"duckdb_append_int16 failed"
			}
		}

		inline fun appendInt8(i: Byte) = apply {
			duckdb_append_int8(handle.value, i).handleAppendError {
				"duckdb_append_int8() failed"
			}
		}

		fun appendNull() = apply {
			duckdb_append_null(handle.value).handleAppendError {
				"duckdb_append_null failed"
			}
		}

		fun appendFloat(i: Float) = apply {
			duckdb_append_float(handle.value, i).handleAppendError {
				"duckdb_append_float failed"
			}
		}

		fun appendDouble(i: Double) = apply {
			duckdb_append_double(handle.value, i).handleAppendError {
				"duckdb_append_double failed"
			}
		}

		fun appendDate(i: CValue<duckdb_date>) = apply {
			duckdb_append_date(handle.value, i).handleAppendError {
				"duckdb_append_double failed"
			}
		}

		fun appendBoolean(i: Boolean) = apply {
			duckdb_append_bool(handle.value, i).handleAppendError {
				"duckdb_append_bool failed"
			}
		}

		fun appendVarchar(i: String) = apply {
			duckdb_append_varchar(handle.value, i).handleAppendError {
				"duckdb_append_varchar failed"
			}
		}


	}

	fun row(block: Row.() -> Unit) {
		Row().block()
		duckdb_appender_end_row(handle.value).handleAppendError { "duckdb_appender_end_row failed" }
	}

	fun flush() {
		if (duckdb_appender_flush(handle.value) == DuckDBError)
			error("Appender::flush() failed: ${duckdb_appender_error(handle.value)?.toKString()}")
	}

	override fun close() {
		if (duckdb_appender_close(handle.value) == DuckDBError)
			error("Appender::close(): failed ${duckdb_appender_error(handle.value)?.toKString()}")

		duckdb_appender_destroy(handle.ptr)
	}
}