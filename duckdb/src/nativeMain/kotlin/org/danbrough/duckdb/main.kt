@file:OptIn(ExperimentalForeignApi::class)

package org.danbrough.duckdb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.CValue
import kotlinx.cinterop.CVariable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BIGINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BIT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BOOLEAN
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DATE
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DOUBLE
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_FLOAT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INTEGER
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INTERVAL
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INVALID
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_SMALLINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIME
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_TZ
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TINYINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UBIGINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UINTEGER
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_USMALLINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UTINYINT
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_column_data
import org.danbrough.duckdb.cinterops.duckdb_column_name
import org.danbrough.duckdb.cinterops.duckdb_column_type
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_nullmask_data
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_result_type
import org.danbrough.duckdb.cinterops.duckdb_result_typeVar
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_type
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.idx_t
import platform.posix.fflush
import platform.posix.int32_t
import platform.posix.printf
import platform.posix.stdout
import platform.posix.stdout_


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


fun printResult(result: duckdb_result) {


	val zero: idx_t = 0.convert()
	val one: idx_t = 1.convert()

	val rowCount: idx_t = duckdb_row_count(result.ptr)
	val colCount: idx_t = duckdb_column_count(result.ptr)
	val idData = duckdb_column_data(result.ptr, zero)!!.reinterpret<IntVar>()
	val idMask = duckdb_nullmask_data(result.ptr, zero)!!
	val nameData = duckdb_column_data(result.ptr, one)!!.reinterpret<CPointerVar<ByteVar>>()
	val nameMask = duckdb_nullmask_data(result.ptr, one)!!


	/*
	duckdb_type duckdb_column_type(
  duckdb_result *result,
  idx_t col
);
	 */

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
				"SHOW ALL TABLES",
				result.ptr
			).handleDuckDbError { "duckdb_query failed" }
			println("#### SHOW ALL TABLES")
			printResult(result)
			duckdb_destroy_result(result.ptr)

			duckdb_query(
				con.value,
				"SELECT * FROM STUFF",
				result.ptr
			).handleDuckDbError { "duckdb_query failed" }
			println("#### SELECT * FROM STUFF")
			printResult(result)
			duckdb_destroy_result(result.ptr)

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