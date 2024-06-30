package org.danbrough.duckdb

expect interface DatabasePeer : AutoCloseable

expect class Database(path: String?, config: DatabaseConfig?) : DatabasePeer {

  val path: String?
  val config: DatabaseConfig?

  fun connect(): Connection

  override fun close()
}

fun <R : Any> Database.connect(block: Connection.() -> R): R = connect().use(block)
fun <R> duckdb(path: String? = null, config: DatabaseConfig? = null, block: Database.() -> R) =
  Database(path, config).use(block)
