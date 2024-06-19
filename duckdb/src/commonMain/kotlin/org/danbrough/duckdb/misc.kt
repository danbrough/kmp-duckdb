package org.danbrough.duckdb

import kotlinx.cinterop.toKString
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_result_error
import platform.posix.errno
import platform.posix.strerror

val log = klog.logger("DUCKDB")

inline fun UInt.handleDuckDbError(msg: () -> String) {
	if (this == DuckDBError) {
		log.trace { "got an error" }
		"${msg()} message: ${strerror(errno)?.toKString()}".also {
			log.error { it }
			throw Exception(it)
		}
	}
}

