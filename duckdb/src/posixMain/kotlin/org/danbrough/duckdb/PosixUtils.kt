package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_MAP
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_column_name
import org.danbrough.duckdb.cinterops.duckdb_column_type
import org.danbrough.duckdb.cinterops.duckdb_config_count
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_get_config_flag
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.idx_t
import platform.posix.fflush
import platform.posix.printf
import platform.posix.size_t
import platform.posix.stdout

object PosixUtils {

	/**
	 * Dump table to stdout
	 */

	fun printResult(result: duckdb_result) {
		val zero: idx_t = 0.convert()
		val rowCount: idx_t = duckdb_row_count(result.ptr)
		val colCount: idx_t = duckdb_column_count(result.ptr)

		log.trace { "printResult(): rowCount: $rowCount colCount: $colCount" }
		for (col in zero until colCount) {
			val colType = duckdb_column_type(result.ptr, col)
			val colName = duckdb_column_name(result.ptr, col)
			val enumValue = DuckDbType.entries[colType.convert()]
			printf("COLUMN $col: type:$enumValue name:${colName!!.toKString()}\n")
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

	@OptIn(UnsafeNumber::class)
	fun duckdbConfigFlags(): Map<String, String> = buildMap {
		memScoped {
			val count = duckdb_config_count()
			log.trace { "duckdb_config_count => $count" }
			val cName: CPointerVarOf<CPointer<ByteVar>> = alloc()
			val cDescription: CPointerVarOf<CPointer<ByteVar>> = alloc()
			var index: size_t = 0.convert()
			while (index < count) {
				duckdb_get_config_flag(
					index++,
					cName.ptr,
					cDescription.ptr
				).handleDuckDbError { "duckdb_get_config_flag failed" }

				val name = cName.value!!.toKString()
				val description = cDescription.value!!.toKString()
				put(name, description)
				//println("$name:\t$description")
			}
		}
	}
}