package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect

actual interface NativeConnection : NativeObject<duckdb_connectionVar>, AutoCloseable

actual class Connection(actual val database: Database) : NativeConnection {

  override val handle: duckdb_connectionVar = database.scope.alloc<duckdb_connectionVar>().also {
    duckdb_connect(database.handle.value, it.ptr).handleDuckDbError {
      "duckdb_connect failed"
    }
  }

  override fun close() {
    log.trace { "DuckDBConnection::close()" }
    duckdb_disconnect(handle.ptr)
  }

  actual fun query(sql: String): Result = Result(this, sql)

  actual fun append(table: String): Appender = Appender(this, table, database.scope.alloc())
  actual fun prepareStatement(sql: String) = PreparedStatement(this, sql, database.scope.alloc())


  /*








	fun append(table: String) = Appender(this, table, memScope.alloc())

	fun append(table: String, block: Appender.() -> Unit) = append(table).use(block)
   */
}

fun Connection.append(table: String, block: Appender.() -> Unit) = append(table).use(block)
