package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_configVar
import org.danbrough.duckdb.cinterops.duckdb_config_count
import org.danbrough.duckdb.cinterops.duckdb_create_config
import org.danbrough.duckdb.cinterops.duckdb_destroy_config
import org.danbrough.duckdb.cinterops.duckdb_get_config_flag
import org.danbrough.duckdb.cinterops.duckdb_set_config
import platform.posix.free
import platform.posix.size_t

class DuckDBConfig(memScope: MemScope,override val handle: duckdb_configVar) : NativeObject<duckdb_configVar>(memScope) {

	enum class AccessMode {
		AUTOMATIC, READ_ONLY, READ_WRITE;
	}

	init {
		duckdb_create_config(handle.ptr).handleDuckDbError {
			"duckdb_create_config failed"
		}
	}

	constructor(memScope: MemScope,handle: duckdb_configVar, options: Map<String, String>) : this(memScope,handle) {
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


fun MemScope.duckdbConfig() = DuckDBConfig(this,alloc())

fun duckdbConfigFlags(): Map<String, String> = buildMap {
	memScoped {
		val count = duckdb_config_count()
		log.trace { "duckdb_config_count => $count" }
		val cName: CPointerVarOf<CPointer<ByteVar>> = alloc()
		val cDescription: CPointerVarOf<CPointer<ByteVar>> = alloc()
		var index: size_t = 0.convert()
		while (index < count) {
			duckdb_get_config_flag(
				index++,
				cName.ptr,
				cDescription.ptr
			).handleDuckDbError { "duckdb_get_config_flag failed" }

			val name = cName.value!!.toKString()
			val description = cDescription.value!!.toKString()


			put(name, description)
			//println("$name:\t$description")
		}
	}
}