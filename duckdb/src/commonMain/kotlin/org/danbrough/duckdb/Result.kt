package org.danbrough.duckdb

import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_column_name
import org.danbrough.duckdb.cinterops.duckdb_column_type
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.idx_t
import platform.posix.fflush
import platform.posix.printf
import platform.posix.stdout


@Suppress("MemberVisibilityCanBePrivate")
class Result(conn: Connection, val sql: String, override val handle: duckdb_result) :
	NativeObject<duckdb_result> {

	init {
		duckdb_query(conn.handle.value, sql, handle.ptr).handleDuckDbError {
			"query: $sql"
		}
	}

	override fun close() {
		duckdb_destroy_result(handle.ptr)
	}

	fun printResult() {

		val zero: idx_t = 0.convert()
		val rowCount: idx_t = duckdb_row_count(handle.ptr)
		val colCount: idx_t = duckdb_column_count(handle.ptr)

		log.trace { "rowCount: $rowCount colCount: $colCount" }
		for (col in zero until colCount) {
			val colType = duckdb_column_type(handle.ptr, col)
			val colName = duckdb_column_name(handle.ptr, col)
			printf("COLUMN $col: type:${duckdbTypeToString(colType)} name:${colName!!.toKString()}\n")
		}

		for (row in zero until rowCount) {
			for (col in zero until colCount) {
				if (col > zero) printf("\t")
				val str = duckdb_value_varchar(handle.ptr, col, row)
				printf("%s", str)
				duckdb_free(str)
			}
			printf("\n")
		}
		printf("\n")
		fflush(stdout)
	}

}