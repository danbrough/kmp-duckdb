package org.danbrough.duckdb

@OptIn(ExperimentalStdlibApi::class)
expect interface DatabasePeer : AutoCloseable

@OptIn(ExperimentalStdlibApi::class)
expect class Database(path: String?, config: DatabaseConfig?) : DatabasePeer, AutoCloseable {

  val path: String?
  val config: DatabaseConfig?

  fun connect(): Connection

  override fun close()
}

fun <R : Any> Database.connect(block: Connection.() -> R): R = connect().use(block)

fun <R : Any> duckdb(
  path: String? = null,
  config: DatabaseConfig? = null,
  block: Database.() -> R
) =
  Database(path, config).use(block)
