package org.danbrough.duckdb

actual interface DatabasePeer : AutoCloseable


actual class Database actual constructor(
  actual val path: String?,
  actual val config: DatabaseConfig?
) : NativePeer(), DatabasePeer {

  companion object {
    @JvmStatic
    external fun create(path: String?): Long

    @JvmStatic
    external fun destroy(handle: Long)
  }

  actual fun connect(): Connection {
    TODO("Not yet implemented")
  }

  override fun nativeCreate(): Long = create(path)


  override fun nativeDestroy(ref: Long) = destroy(ref)


}