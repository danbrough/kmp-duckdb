package org.danbrough.duckdb

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect

@Suppress("MemberVisibilityCanBePrivate")
class Connection2(
	 val memScope: MemScope,
	db: Database2
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

	fun query(sql: String) = Result2(this,memScope.alloc(),sql)

	fun <R> query(sql: String, block: Result2.() -> R) = query(sql).use(block)

	fun prepareStatement(sql: String) = PreparedStatement2(this, sql, memScope.alloc())
	fun prepareStatement(sql: String, block: PreparedStatement2.() -> Unit) =
		prepareStatement(sql).use(block)

	fun append(table: String) = Appender2(this, table, memScope.alloc())

	fun append(table: String, block: Appender2.() -> Unit) = append(table).use(block)
}