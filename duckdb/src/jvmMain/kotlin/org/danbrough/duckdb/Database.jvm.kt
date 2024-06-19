package org.danbrough.duckdb

actual class Database : DatabasePeer {
  actual val scope: RootScope
    get() = TODO("Not yet implemented")

  actual fun connect(): Connection {
    TODO("Not yet implemented")
  }

  actual override fun close() {
  }

}

actual interface DatabasePeer : AutoCloseable