package org.danbrough.duckdb

@OptIn(ExperimentalStdlibApi::class)
expect interface NativePreparedStatement : AutoCloseable
expect class PreparedStatement : NativePreparedStatement {

  val connection: Connection

  val sql: String

  inline fun bindNull(index: Int): PreparedStatement

  inline fun <T : Any> bind(index: Int, value: T): PreparedStatement

  fun <R> execute(block: Result.() -> R): R

  fun clearBindings()

  override fun close()

}

fun PreparedStatement.execute():Unit = execute {  }