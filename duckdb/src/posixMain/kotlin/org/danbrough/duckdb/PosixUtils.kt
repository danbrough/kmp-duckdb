package org.danbrough.duckdb

import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_column_name
import org.danbrough.duckdb.cinterops.duckdb_column_type
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.idx_t
import platform.posix.fflush
import platform.posix.printf
import platform.posix.stdout

object PosixUtils {

	/**
	 * Dump table to stdout
	 */

	fun printResult(result: duckdb_result) {
		val zero: idx_t = 0.convert()
		val rowCount: idx_t = duckdb_row_count(result.ptr)
		val colCount: idx_t = duckdb_column_count(result.ptr)

		log.trace { "rowCount: $rowCount colCount: $colCount" }
		for (col in zero until colCount) {
			val colType = duckdb_column_type(result.ptr, col)
			val colName = duckdb_column_name(result.ptr, col)
			printf("COLUMN $col: type:${duckdbTypeToString(colType)} name:${colName!!.toKString()}\n")
		}

		for (row in zero until rowCount) {
			for (col in zero until colCount) {
				if (col > zero) printf("\t")
				val str = duckdb_value_varchar(result.ptr, col, row)
				printf("%s", str)
				duckdb_free(str)
			}
			printf("\n")
		}
		printf("\n")
		fflush(stdout)
	}

}