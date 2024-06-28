package org.danbrough.duckdb

import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect

actual interface ConnectionPeer : NativePeer<duckdb_connectionVar>, AutoCloseable

actual class Connection(actual val database: Database) : ConnectionPeer {

  override val handle: duckdb_connectionVar = nativeHeap.alloc<duckdb_connectionVar>()

  init {
    duckdb_connect(database.handle.value, handle.ptr).handleDuckDbError {
      "duckdb_connect failed"
    }
  }

  actual override fun close() {
    log.trace { "DuckDBConnection::close()" }
    duckdb_disconnect(handle.ptr)
    nativeHeap.free(handle)
  }

  actual fun query(sql: String): Result = Result(this, sql)

  actual fun append(table: String): Appender = Appender(this, table)

  actual fun prepareStatement(sql: String) = PreparedStatement(this, sql)


  /*








	fun append(table: String) = Appender(this, table, memScope.alloc())

	fun append(table: String, block: Appender.() -> Unit) = append(table).use(block)
   */
}

