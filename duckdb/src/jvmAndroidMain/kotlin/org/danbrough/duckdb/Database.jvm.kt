package org.danbrough.duckdb

import org.danbrough.duckdb.Connection.Companion

actual interface DatabasePeer : AutoCloseable


actual class Database actual constructor(
  actual val path: String?,
  actual val config: DatabaseConfig?
) : NativePeer(), DatabasePeer {

  companion object {
    @JvmStatic
    external fun create(path: String?, config: DatabaseConfig?): Long

    @JvmStatic
    external fun destroy(handle: Long)
    @JvmStatic
    external fun test()
  }

  override val handle: Long = create(path, config)

  actual fun connect() = Connection(this)

  override fun nativeDestroy() = destroy(handle)
}