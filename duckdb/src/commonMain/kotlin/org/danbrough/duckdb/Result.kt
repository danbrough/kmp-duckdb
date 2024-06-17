package org.danbrough.duckdb

import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count


@Suppress("MemberVisibilityCanBePrivate")
class Result(conn: Connection, val sql: String, override val handle: duckdb_result) :
	NativeObject<duckdb_result> {

	val rowCount: ULong by lazy {
		duckdb_row_count(handle.ptr)
	}

	val columnCount:ULong by  lazy {
		duckdb_column_count(handle.ptr)
	}

	init {
		duckdb_query(conn.handle.value, sql, handle.ptr).handleDuckDbError {
			"query: $sql"
		}
	}

	override fun close() = duckdb_destroy_result(handle.ptr)

}