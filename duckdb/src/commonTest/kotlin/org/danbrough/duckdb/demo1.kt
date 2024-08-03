package org.danbrough.duckdb

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import org.danbrough.xtras.support.getEnv


fun demo1() {
  log.info { "demo1" }
  runBlocking {
/*    duckdb(
      "${getEnv("HOME")}/.habitrack/hb.db",
      databaseConfig(AccessMode.READ_WRITE)
    ) {

      connect {
        log.trace { "connected()" }
        var id = query("SELECT id FROM EVENT ORDER BY id DESC LIMIT 1") { get<ULong>(0, 0) }
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
    }*/
    duckdb(
      "${getEnv("HOME")}/.habitrack/hb.db",
      databaseConfig(AccessMode.READ_ONLY)
    ) {

      connect {
        query("select {id:event.id,time:event.time,type:enum_code(event.type),count:event.count}::JSON from event") {

          log.trace { "rowCount: $rowCount colCount: $columnCount" }

          for (n in 0 until rowCount) {
            log.debug { get<String>(n, 0) }
          }
        }
      }
    }
  }
}


