@file:Suppress("ConvertSecondaryConstructorToPrimary")

package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_configVar
import org.danbrough.duckdb.cinterops.duckdb_create_config
import org.danbrough.duckdb.cinterops.duckdb_destroy_config
import org.danbrough.duckdb.cinterops.duckdb_set_config

actual interface DatabaseConfigPeer : AutoCloseable, NativePeer<duckdb_configVar>

actual class DatabaseConfig : DatabaseConfigPeer {
  override val handle: duckdb_configVar = nativeHeap.alloc()

  init {
    duckdb_create_config(handle.ptr).handleDuckDbError {
      "duckdb_create_config failed"
    }
  }

  actual operator fun set(name: String, option: String) {
    duckdb_set_config(handle.value, name, option).handleDuckDbError {
      "duckdb_set_config [$name = $option] failed"
    }
  }

  fun setMaxMemory(value: String) {
    set("mac_memory", value)
  }


  actual override fun close() {
    duckdb_destroy_config(handle.ptr)
    nativeHeap.free(handle)
  }


}