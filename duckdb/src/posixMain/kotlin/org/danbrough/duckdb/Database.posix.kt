@file:Suppress("ConvertSecondaryConstructorToPrimary")

package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_open_ext

actual interface DatabasePeer : NativePeer<duckdb_databaseVar>, AutoCloseable

@Suppress("unused")
actual class Database actual constructor(
  actual val path: String?,
  actual val config: DatabaseConfig?
) : DatabasePeer {

  override val handle: duckdb_databaseVar = nativeHeap.alloc<duckdb_databaseVar>()

  init {
    memScoped {
      val error: CPointerVarOf<CPointer<ByteVar>> = alloc()

      log.trace { "Database::calling open with path: $path config:$config .." }
      if (duckdb_open_ext(
          path,
          handle.ptr,
          config?.handle?.value,
          error.ptr
        ) == DuckDBError
      ) {
        error("duckdb_open_ext failed: ${error.value?.toKString()}")
      }
    }
  }

  actual fun connect(): Connection = Connection(this)

  actual override fun close() {
    log.trace { "Database::close()" }
    duckdb_close(handle.ptr)
    nativeHeap.free(handle)
  }
}