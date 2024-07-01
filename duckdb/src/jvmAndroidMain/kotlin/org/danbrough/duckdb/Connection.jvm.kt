package org.danbrough.duckdb

actual interface ConnectionPeer : AutoCloseable

actual class Connection(actual val database: Database) : NativePeer(), ConnectionPeer {


  companion object {
    @JvmStatic
    external fun create(db: Long): Long

    @JvmStatic
    external fun destroy(handle: Long)

    @JvmStatic
    external fun query(conn: Long, sql: String): Long
  }

  override var handle: Long = create(database.handle)

  actual fun query(sql: String) = Result(Companion.query(handle, sql))

  actual fun prepareStatement(sql: String): PreparedStatement {
    TODO("Not yet implemented")
  }

  override fun nativeDestroy()=destroy(handle)

  actual fun append(table: String): Appender {
    TODO("Not yet implemented")
  }


}

