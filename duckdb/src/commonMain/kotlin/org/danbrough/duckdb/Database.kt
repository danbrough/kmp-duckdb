package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_open_ext

class Database(private val memScope: MemScope) : NativeObject<duckdb_databaseVar> {

	override val handle: duckdb_databaseVar = memScope.alloc()

	constructor(memScope: MemScope, path: String?) : this(memScope) {
		duckdb_open(path, handle.ptr).handleDuckDbError { "duckdb_open $path failed" }
		log.trace { "opened db at $path" }
	}

	constructor(
		memScope: MemScope,
		path: String? = null,
		config: DatabaseConfig
	) : this(memScope) {
		config.use {
			val err: CPointerVarOf<CPointer<ByteVar>> = memScope.alloc()
			duckdb_open_ext(path, handle.ptr, config.handle.value, err.ptr).also {
				it.handleDuckDbError {
					"duckdb_open_ext failed: ${err.value?.toKString()}"
				}
			}
		}
	}

	fun connect(): Connection = Connection(memScope,this)

	override fun close() {
		log.trace { "DuckDB::close()" }
		duckdb_close(handle.ptr)
	}

}

fun MemScope.duckdb(path: String?) = Database(this, path)
//fun MemScope.duckdb(path: String?, config: DuckDBConfig) = DuckDB(this, path, config)