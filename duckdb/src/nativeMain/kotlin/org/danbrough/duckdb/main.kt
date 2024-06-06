@file:OptIn(ExperimentalForeignApi::class)

package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.CValue
import kotlinx.cinterop.CVariable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_result_type
import org.danbrough.duckdb.cinterops.duckdb_result_typeVar
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.idx_t
import platform.posix.printf


const val dbPath = "/tmp/test.db"

fun test1() {
	memScoped {

		duckdb(dbPath).use { db ->
			log.debug { "opened db" }

			db.connect().use { conn ->
				log.debug { "connected" }
				val result: duckdb_result = alloc()
				duckdb_query(
					conn.handle.value,
					"SELECT * FROM STUFF",
					result.ptr
				).handleDuckDbError { "query failed" }
				val count = duckdb_row_count(result.ptr)
				log.debug { "retrieved $count rows" }
			}
		}
	}
}


fun test2() {
	memScoped {
		val db: duckdb_databaseVar = alloc()
		val con: duckdb_connectionVar = alloc()
		runCatching {
			duckdb_open(dbPath, db.ptr).handleDuckDbError { "duckdb_open $dbPath failed" }
			log.trace { "opened db" }
			duckdb_connect(db.value, con.ptr).handleDuckDbError { "duckdb_connect failed" }
			log.trace { "connected" }
			val result: duckdb_result = alloc()
			duckdb_query(
				con.value,
				"SELECT * FROM STUFF",
				result.ptr
			).handleDuckDbError { "duckdb_query failed" }


			val rowCount: idx_t = duckdb_row_count(result.ptr)
			val colCount: idx_t = duckdb_column_count(result.ptr)
			log.trace { "rowCount: $rowCount colCount: $colCount" }
			val zero: idx_t = 0.convert()
			for (row in zero until rowCount) {
				for (col in zero until colCount) {
					if (col > zero) printf("\t")
					val str = duckdb_value_varchar(result.ptr, col, row)
					printf("%s", str)
					duckdb_free(str)
				}
				printf("\n")
			}


			/*
			idx_t row_count = duckdb_row_count(&result);
idx_t column_count = duckdb_column_count(&result);
for (idx_t row = 0; row < row_count; row++) {
    for (idx_t col = 0; col < column_count; col++) {
        if (col > 0) printf(",");
        auto str_val = duckdb_value_varchar(&result, col, row);
        printf("%s", str_val);
        duckdb_free(str_val);
   }
   printf("\n");
}
			 */
		}.exceptionOrNull().also {
			if (it != null) log.error(it) { "error: ${it.message}" }
			duckdb_disconnect(con.ptr)
			duckdb_close(db.ptr)
		}
	}

}

fun main(args: Array<String>) {
	//klog.kloggingDisabled() //to disable klog  
	log.info { "main()" }

	val flags = duckdbConfigFlags()
	println()
	println("#Config flags")
	flags.forEach {
		println("- ${it.key}:\t${it.value}")
	}
	println()

	test2()
}