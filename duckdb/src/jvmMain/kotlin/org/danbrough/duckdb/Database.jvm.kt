package org.danbrough.duckdb

actual class Database actual constructor(
  path: String?,
  config: DatabaseConfig?
) : DatabasePeer {


  actual fun connect(): Connection {
    TODO("Not yet implemented")
  }

  actual override fun close() {
  }

  actual var path: String?
    get() = TODO("Not yet implemented")
    set(value) {}
  actual var config: DatabaseConfig?
    get() = TODO("Not yet implemented")
    set(value) {}

}

actual interface DatabasePeer : AutoCloseable