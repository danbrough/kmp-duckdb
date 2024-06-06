package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_open_ext

class DuckDB(override val handle: duckdb_databaseVar) : NativeObject<duckdb_databaseVar>() {

	constructor(handle: duckdb_databaseVar, path: String?) : this(handle) {
		duckdb_open(path, handle.ptr).handleDuckDbError { "duckdb_open $path failed" }
	}

	constructor(
		handle: duckdb_databaseVar,
		path: String? = null,
		config: DuckDBConfig
	) : this(handle) {
		memScoped {
			config.use {
				val err: CPointerVarOf<CPointer<ByteVar>> = alloc()
				duckdb_open_ext(path, handle.ptr, config.handle.value, err.ptr).also {
					it.handleDuckDbError {
						"duckdb_open_ext failed: ${err.value?.toKString()}"
					}
				}
			}
		}
	}

	fun connect(memScope: MemScope): DuckDBConnection = DuckDBConnection(memScope.alloc())

	override fun close() {
		log.trace { "DuckDB::close()" }
		duckdb_close(handle.ptr)
	}

}

fun MemScope.duckDB(path: String?) = DuckDB(alloc(), path)
fun MemScope.duckDB(path: String?, config: DuckDBConfig) = DuckDB(alloc(), path, config)