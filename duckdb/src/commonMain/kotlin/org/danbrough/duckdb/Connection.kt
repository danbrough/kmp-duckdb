package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result

class Connection(
	private val memScope: MemScope,
	db: Database
) : NativeObject<duckdb_connectionVar> {

	override val handle: duckdb_connectionVar = memScope.alloc()

	init {
		duckdb_connect(db.handle.value, handle.ptr).handleDuckDbError {
			"duckdb_connect failed"
		}
	}


	override fun close() {
		log.trace { "DuckDBConnection::close()" }
		duckdb_disconnect(handle.ptr)
	}

	fun query(sql: String, result: duckdb_result) {
		duckdb_query(handle.value, sql, result.ptr).handleDuckDbError {
			"query: $sql"
		}
	}

	fun query(sql: String) = Result(this, sql, memScope.alloc())
}