package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.toKString
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_database
import org.danbrough.duckdb.cinterops.duckdb_result
import platform.posix.errno
import platform.posix.strerror

inline fun UInt.handleDuckDbError(msg: () -> String) {
  if (this == DuckDBError) {
    log.trace { "got an error" }
    "${msg()} message: ${strerror(errno)?.toKString()}".also {
      log.error { it }
      throw Exception(it)
    }
  }
}


