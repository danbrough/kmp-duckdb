package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_database
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_open

actual interface DatabaseHandle : NativeObject<duckdb_databaseVar>, AutoCloseable

@Suppress("unused")
actual class Database(actual val scope: RootScope) : DatabaseHandle {

  //actual val scope: RootScope = scope

  override val handle: duckdb_databaseVar = scope.alloc()

  constructor(memScope: RootScope, path: String?) : this(memScope) {
    duckdb_open(path, handle.ptr).handleDuckDbError { "duckdb_open $path failed" }
    log.trace { "opened db at $path" }
  }

  actual fun connect(): Connection = Connection(this)

  //actual fun <R : Any> connect(block: Connection.() -> R): R = connect().use(block)

  override fun close() {
    log.trace { "Database::close()" }
    duckdb_close(handle.ptr)
  }
}