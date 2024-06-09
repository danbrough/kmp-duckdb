package org.danbrough.duckdb

import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_column_name
import org.danbrough.duckdb.cinterops.duckdb_column_type
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.idx_t
import platform.posix.fflush
import platform.posix.printf
import platform.posix.stdout


@Suppress("MemberVisibilityCanBePrivate")
class Result(conn: Connection, val sql: String, override val handle: duckdb_result) :
	NativeObject<duckdb_result> {

	init {
		duckdb_query(conn.handle.value, sql, handle.ptr).handleDuckDbError {
			"query: $sql"
		}
	}

	override fun close() {
		duckdb_destroy_result(handle.ptr)
	}



}