package org.danbrough.duckdb

expect interface DatabasePeer : AutoCloseable

expect class Database(path: String? = null, config: DatabaseConfig? = null) : DatabasePeer {

  val path: String?
  val config: DatabaseConfig?

  fun connect(): Connection

  override fun close()
}

fun <R : Any> Database.connect(block: Connection.() -> R): R = connect().use(block)
