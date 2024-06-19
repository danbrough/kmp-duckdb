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


fun test1(cmdArgs: DemoArgs) {
  memScoped {
    duckdb(cmdArgs.databasePath) {
      log.debug { "opened db" }

      connect {
        log.debug { "connected" }

        query("SHOW ALL TABLES").use { result ->
          println("#### SHOW ALL TABLES")
          log.debug { "table count: ${result.rowCount}" }
          PosixUtils.printResult(result.handle)
        }

        query("SELECT * FROM duckdb_extensions()").use { result ->
          println("#### SELECT * FROM duckdb_extensions()")
          PosixUtils.printResult(result.handle)
        }
      }
    }
  }
}

fun test2(cmdArgs: DemoArgs) {
  memScoped {
    val db: duckdb_databaseVar = alloc()
    val conn: duckdb_connectionVar = alloc()
    runCatching {
      duckdb_open(
        cmdArgs.databasePath,
        db.ptr
      ).handleDuckDbError { "duckdb_open $cmdArgs.databasePath failed" }
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

fun insertTest(cmdArgs: DemoArgs) {
  log.info { "insertTest()" }
  memScoped {
    duckdb(cmdArgs.databasePath) {
      log.debug { "opened db" }

      connect {
        log.debug { "connected" }

        query("CREATE OR REPLACE TABLE things(id INT PRIMARY KEY, NAME VARCHAR)") {
          log.trace { "rowsChanged: $rowsChanged" }
        }

        append("things") {
          repeat(100) {
            row {
              appendInt32(it).appendVarchar("Item: $it")
            }
            if (it % 10 == 0 && it != 0) {
              log.trace { "flushing at $it" }
              flush()
            }
          }
        }

        prepareStatement("SELECT * FROM things WHERE id > $1") {
          bindInt32(1U, 90)

          executeWithResult {
            log.warn { "received: $rowCount rows" }
            PosixUtils.printResult(handle)
          }
        }

        //select {id:event.id,time:event.time,type:event.type,count:event.count}::JSON from event;
        query("SELECT {id:things.id,name:things.name}::JSON FROM things") {
          PosixUtils.printResult(handle)
        }
      }
    }
  }
}


open class DemoArgs() : CommandLine() {
  lateinit var runJob: DemoArgs.() -> Unit

  fun run(args: Array<String>, block: DemoArgs.() -> Unit) {
    runJob = block
    main(args)
  }

  override fun run() = runJob()
}

fun demo1(args: Array<String>) {
  //klog.kloggingDisabled() //to disable klog
  DemoArgs().run(args) {

    val flags = PosixUtils.duckdbConfigFlags()
    println()
    println("#Config flags")
    flags.forEach {
      println("- ${it.key}:\t${it.value}")
    }
    println()

    //test1(this)
    //test2(this)
    insertTest(this)
  }
}

