package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_configVar
import org.danbrough.duckdb.cinterops.duckdb_create_config
import org.danbrough.duckdb.cinterops.duckdb_destroy_config
import org.danbrough.duckdb.cinterops.duckdb_set_config

class DatabaseConfig(memScope: MemScope, override val handle: duckdb_configVar) :
	NativeObject<duckdb_configVar> {

	enum class AccessMode {
		AUTOMATIC, READ_ONLY, READ_WRITE;
	}

	init {
		duckdb_create_config(handle.ptr).handleDuckDbError {
			"duckdb_create_config failed"
		}
	}

	constructor(memScope: MemScope, handle: duckdb_configVar, options: Map<String, String>) : this(
		memScope,
		handle
	) {
		options.forEach {
			set(it.key, it.value)
		}
	}

	operator fun set(name: String, option: String) {
		duckdb_set_config(handle.value, name, option).handleDuckDbError {
			"duckdb_set_config [$name = $option] failed"
		}
	}

	fun setAccessMode(mode: AccessMode) {
		set("access_mode", mode.name)
	}

	fun setMaxMemory(value: String) {
		set("mac_memory", value)
	}

	fun setThreads(count: Int) {
		set("threads", count.toString())
	}

	override fun close() {
		duckdb_destroy_config(handle.ptr)
	}
}


fun MemScope.duckdbConfig() = DatabaseConfig(this, alloc())

