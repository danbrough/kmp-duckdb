@file:Suppress("ConvertSecondaryConstructorToPrimary")

package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
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

@Suppress("unused", "CanBePrimaryConstructorProperty")
actual class Database actual constructor(
  actual val path: String?,
  actual val config: DatabaseConfig?
) :
  DatabasePeer {


  override val handle: duckdb_databaseVar = nativeHeap.alloc()

  init {
    //val error: CPointerVarOf<CPointer<ByteVarOf<Byte>>> = nativeHeap.alloc()
    val error: CPointerVarOf<CPointer<ByteVar>> = nativeHeap.alloc()

    val errMessage =
      if (duckdb_open_ext(path, handle.ptr, config?.handle?.value, error.ptr) == DuckDBError) {
        "duckdb_open_ext failed: ${error.value?.toKString()}"
      } else null

    if (errMessage != null) log.error { errMessage }

    duckdb_free(error.ptr)

    if (errMessage != null) error(errMessage)
  }


  actual fun connect(): Connection = Connection(this)

  actual override fun close() {
    log.trace { "Database::close()" }
    duckdb_close(handle.ptr)
    nativeHeap.free(handle)
  }
}