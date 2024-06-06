package org.danbrough.duckdb

import kotlinx.cinterop.toKString
import org.danbrough.duckdb.cinterops.DuckDBError
import platform.posix.errno
import platform.posix.strerror

internal val log = klog.logger("DUCKDB")

internal inline fun UInt.handleDuckDbError(msg: () -> String) {
	if (this == DuckDBError) {
		log.trace { "got an error" }
		"${msg()} message: ${strerror(errno)?.toKString()}".also {
			log.error { it }
			throw Exception(it)
		}
	}
}

