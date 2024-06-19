package org.danbrough.duckdb
expect interface DatabasePeer : AutoCloseable

expect class Database : DatabasePeer {
  val scope: RootScope

  fun connect(): Connection

  override fun close()
}

fun <R : Any> Database.connect(block: Connection.() -> R): R = connect().use(block)
