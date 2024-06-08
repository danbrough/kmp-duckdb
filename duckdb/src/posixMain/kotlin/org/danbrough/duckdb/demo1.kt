@file:OptIn(ExperimentalForeignApi::class)

package org.danbrough.duckdb

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_open
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cli.CommandLine


fun test1() {
	memScoped {

		duckdb(cmdArgs.databasePath).use { db ->
			log.debug { "opened db" }

			db.connect().use { conn ->
				log.debug { "connected" }
				val sql = "SHOW ALL TABLES"
				val result: duckdb_result = alloc()
				duckdb_query(conn.handle.value, sql, result.ptr).handleDuckDbError {
					"query: $sql"
				}

				conn.query(sql)

				println("#### SHOW ALL TABLES")
				PosixUtils.printResult(result)
				duckdb_destroy_result(result.ptr)
			}
		}
	}
}

fun test2() {
	memScoped {
		val db: duckdb_databaseVar = alloc()
		val conn: duckdb_connectionVar = alloc()
		runCatching {
			duckdb_open(cmdArgs.databasePath, db.ptr).handleDuckDbError { "duckdb_open $cmdArgs.databasePath failed" }
			log.trace { "opened db" }
			duckdb_connect(db.value, conn.ptr).handleDuckDbError { "duckdb_connect failed" }
			log.trace { "connected" }
			val result: duckdb_result = alloc()
			duckdb_query(
				conn.value,
				"SHOW ALL TABLES",
				result.ptr
			).handleDuckDbError { "duckdb_query failed" }
			println("#### SHOW ALL TABLES")
			PosixUtils.printResult(result)
			duckdb_destroy_result(result.ptr)

			duckdb_query(
				conn.value,
				"SELECT * FROM STUFF",
				result.ptr
			).handleDuckDbError { "duckdb_query failed" }
			println("#### SELECT * FROM STUFF")
			PosixUtils.printResult(result)
			duckdb_destroy_result(result.ptr)

		}.exceptionOrNull().also {
			if (it != null) log.error(it) { "error: ${it.message}" }
			duckdb_disconnect(conn.ptr)
			duckdb_close(db.ptr)
		}
	}

}

val cmdArgs = object : CommandLine() {
	override fun run() {
		log.info { "main()" }

		val flags = PosixUtils.duckdbConfigFlags()
		println()
		println("#Config flags")
		flags.forEach {
			println("- ${it.key}:\t${it.value}")
		}
		println()

		test1()
		test2()
	}
}

fun demo1(args: Array<String>) {
	//klog.kloggingDisabled() //to disable klog
	cmdArgs.main(args)
}

