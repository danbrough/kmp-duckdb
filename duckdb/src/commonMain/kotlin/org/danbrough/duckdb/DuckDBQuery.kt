package org.danbrough.duckdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.ptr
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_result


@Suppress("MemberVisibilityCanBePrivate")
class DuckDBQuery(val handle: duckdb_result) : AutoCloseable {
	override fun close() {
		duckdb_destroy_result(handle.ptr)
	}
}