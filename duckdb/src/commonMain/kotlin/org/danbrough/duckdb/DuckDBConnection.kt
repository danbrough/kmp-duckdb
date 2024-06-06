package org.danbrough.duckdb

import kotlinx.cinterop.ptr
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect

class DuckDBConnection(override val handle: duckdb_connectionVar) :
	NativeObject<duckdb_connectionVar>() {
	override fun close() {
		log.trace { "DuckDBConnection::close()" }
		duckdb_disconnect(handle.ptr)
	}
}