package org.danbrough.duckdb

actual interface DatabasePeer : NativePeer, AutoCloseable
actual class Database actual constructor(
  actual val path: String?,
  actual val config: DatabaseConfig?
) : DatabasePeer {
  override var handle: Long = 0L

  actual fun connect(): Connection {
    TODO("Not yet implemented")
  }


  actual override fun close() {
  }

}