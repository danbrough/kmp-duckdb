@file:OptIn(ExperimentalForeignApi::class)

package org.danbrough.duckdb

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped


const val dbPath = "/tmp/test.db"
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
		duckDB(dbPath).use { db ->
			log.debug { "opened db" }

			db.connect(this).use { conn ->
				log.debug { "connected with $conn" }
			}
		}
	}
}