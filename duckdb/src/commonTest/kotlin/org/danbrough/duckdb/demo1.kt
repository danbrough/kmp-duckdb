package org.danbrough.duckdb

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import org.danbrough.xtras.support.getEnv


/*
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
*/

/*fun test2(cmdArgs: DemoArgs) {
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
}*/

fun insertTest(cmdArgs: DemoArgs) {
  log.info { "insertTest()" }
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
            append(it).append("Item: $it")
          }
          if (it % 10 == 0 && it != 0) {
            log.trace { "flushing at $it" }
            flush()
          }
        }
      }

      prepareStatement("SELECT * FROM things WHERE id > $1") {
        bind(1, 90)

        execute {
          log.warn { "received: $rowCount rows" }
          //PosixUtils.printResult(handle)
        }
      }

      //select {id:event.id,time:event.time,type:event.type,count:event.count}::JSON from event;
      query("SELECT {id:things.id,name:things.name}::JSON FROM things") {
        //PosixUtils.printResult(handle)
      }
    }
  }

}


fun dbTest(args: DemoArgs) {
  log.info { "dbTest" }
  runBlocking {
    duckdb(
      "${getEnv("HOME")}/.habitrack/database",
      DatabaseConfig(DatabaseConfig.AccessMode.READ_WRITE)
    ) {

      connect {
        var id = query("SELECT id FROM EVENT ORDER BY id DESC LIMIT 1").getUInt(0UL, 0UL).toInt()
        log.trace { "id: $id" }
        append("event") {
          row {
            append(++id)
            append(Clock.System.now().toEpochMilliseconds().toULong())
            append("BEER")
            append(1)
          }
        }
      }
    }
    duckdb(
      "${getEnv("HOME")}/.habitrack/database",
      DatabaseConfig(DatabaseConfig.AccessMode.READ_ONLY)
    ) {
      val midnight = Clock.System.todayIn(TimeZone.currentSystemDefault())
        .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()

      connect {
        query("select {id:event.id,time:event.time,type:enum_code(event.type),count:event.count}::JSON from event WHERE time >= $midnight") {

          log.trace { "rowCount: $rowCount colCount: $columnCount" }

          for (n in 0UL until rowCount) {
            log.debug { getVarchar(n, 0UL) }
          }

          //log.debug { getVarchar(0UL, 1UL) }
          //PosixUtils.printResult(handle)
          //log.debug { getVarchar(0UL, 1UL) }
        }
      }
    }
  }
}

fun demo1(args: Array<String>) {
  //klog.kloggingDisabled() //to disable klog

  DemoArgs(args).run {

/*

    val flags = PosixUtils.duckdbConfigFlags()
    println()
    println("#Config flags")
    flags.forEach {
      println("- ${it.key}:\t${it.value}")
    }
    println()


    val midnight = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault()).date.atStartOfDayIn(TimeZone.currentSystemDefault())
      .toEpochMilliseconds()

    log.warn { "midnight: $midnight" }
    log.info {
      "midnight2: ${
        Clock.System.todayIn(TimeZone.currentSystemDefault())
          .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
      }"
    }
*/


    //test1(this)
    //test2(this)
    //insertTest(this)

    dbTest(this)
  }
}

