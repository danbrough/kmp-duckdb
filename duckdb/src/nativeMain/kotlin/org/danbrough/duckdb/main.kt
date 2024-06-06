@file:OptIn(ExperimentalForeignApi::class)

package org.danbrough.duckdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_result_type
import org.danbrough.duckdb.cinterops.duckdb_result_typeVar
import org.danbrough.duckdb.cinterops.duckdb_row_count


const val dbPath = "/tmp/test.db"

fun test1() {
	memScoped {

		duckdb(dbPath).use { db ->
			log.debug { "opened db" }

			db.connect(this).use { conn ->
				log.debug { "connected" }
				val result = cValue<duckdb_result>()
				duckdb_query(
					conn.handle.value,
					"SELECT * FROM STUFF",
					result.ptr
				).handleDuckDbError { "query failed" }
				//val count = duckdb_row_count(result.ptr)
				//log.debug { "retrieved $count rows" }
			}
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

	memScoped {
		val db: duckdb_databaseVar = alloc()
		val con: duckdb_connectionVar = alloc()
		runCatching {
			duckdb_open(dbPath, db.ptr).handleDuckDbError { "duckdb_open $dbPath failed" }
			log.trace { "opened db" }
			duckdb_connect(db.value, con.ptr).handleDuckDbError { "duckdb_connect failed" }
			log.trace { "connected" }
			val result: duckdb_result = alloc()
			duckdb_query(con.value, "SELECT * FROM STUFF", result.ptr).handleDuckDbError { "duckdb_query failed" }
			val count = duckdb_row_count(result.ptr)
			log.debug { "row count: $count" }
		}.exceptionOrNull().also {
			if (it != null) log.error(it) { "error: ${it.message}" }
			duckdb_disconnect(con.ptr)
			duckdb_close(db.ptr)
		}

	}

}