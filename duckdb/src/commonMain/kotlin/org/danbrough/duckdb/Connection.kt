package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result

@Suppress("MemberVisibilityCanBePrivate")
class Connection(
	 val memScope: MemScope,
	db: Database
) : NativeObject<duckdb_connectionVar> {

	override val handle: duckdb_connectionVar = memScope.alloc()

	init {
		duckdb_connect(db.handle.value, handle.ptr).handleDuckDbError {
			"duckdb_connect failed"
		}
	}

	override fun close() {
		log.trace { "DuckDBConnection::close()" }
		duckdb_disconnect(handle.ptr)
	}

	fun query(sql: String) = Result(this,memScope.alloc(),sql)

	fun <R> query(sql: String, block: Result.() -> R) = query(sql).use(block)

	fun prepareStatement(sql: String) = PreparedStatement(this, sql, memScope.alloc())
	fun prepareStatement(sql: String, block: PreparedStatement.() -> Unit) =
		prepareStatement(sql).use(block)

	fun append(table: String) = Appender(this, table, memScope.alloc())

	fun append(table: String, block: Appender.() -> Unit) = append(table).use(block)
}