package org.danbrough.duckdb

import org.danbrough.duckdb.Connection.Companion

actual interface DatabaseConfigPeer : AutoCloseable

actual class DatabaseConfig : NativePeer(), DatabaseConfigPeer {

  internal companion object {

    @JvmStatic
    external fun create(): Long

    @JvmStatic
    external fun destroy(handle: Long)

    @JvmStatic
    external fun setOption(handle: Long, name: String, value: String)
  }

  override val handle: Long = create()

  override fun nativeDestroy() = destroy(handle)

  actual operator fun set(name: String, option: String) =
    setOption(handle, name, option)



}

