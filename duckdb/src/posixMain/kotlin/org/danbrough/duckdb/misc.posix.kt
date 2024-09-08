package org.danbrough.duckdb

import kotlinx.cinterop.toKString
import org.danbrough.duckdb.cinterops.DuckDBError
import platform.posix.errno
import platform.posix.strerror

inline fun UInt.handleDuckDbError(msg: () -> String) {
  if (this == DuckDBError) {
    log_duckdb.trace { "got an error" }
    "${msg()} message: ${strerror(errno)?.toKString()}".also {
      log_duckdb.error { it }
      throw Exception(it)
    }
  }
}


