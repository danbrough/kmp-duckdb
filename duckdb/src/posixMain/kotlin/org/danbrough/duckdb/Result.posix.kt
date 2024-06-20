package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_get_varchar
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_rows_changed
import org.danbrough.duckdb.cinterops.duckdb_value_uint32
import org.danbrough.duckdb.cinterops.duckdb_value_uint64
import org.danbrough.duckdb.cinterops.duckdb_value_varchar

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

	actual fun getVarchar(row: ULong, col: ULong): String {
		log.trace { "getVarchar() row:$row: col:$col" }
		val data = duckdb_value_varchar(handle.ptr, col, row)
		val s = data!!.toKString()
		duckdb_free(data)
		return s
	}

	actual fun getULong(row: ULong, col: ULong): ULong = duckdb_value_uint64(handle.ptr, col, row)

	actual fun getUInt(row: ULong, col: ULong): UInt = duckdb_value_uint32(handle.ptr, col, row)


}

