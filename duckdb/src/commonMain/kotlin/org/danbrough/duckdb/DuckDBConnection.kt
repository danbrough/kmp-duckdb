package org.danbrough.duckdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_result_typeVar

class DuckDBConnection(memScope: MemScope) :
	NativeObject<duckdb_connectionVar>(memScope) {

	override val handle: duckdb_connectionVar = memScope.alloc()

	override fun close() {
		log.trace { "DuckDBConnection::close()" }
		duckdb_disconnect(handle.ptr)
	}

	fun query(sql: String, result: duckdb_result) {
		duckdb_query(handle.value, sql, result.ptr).handleDuckDbError {
			"query: $sql"
		}
	}
}