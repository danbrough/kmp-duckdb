package org.danbrough.duckdb
expect interface DatabaseHandle : AutoCloseable

expect class Database : DatabaseHandle {
  val scope: RootScope

  fun connect(): Connection
}

fun <R : Any> Database.connect(block: Connection.() -> R): R = connect().use(block)
