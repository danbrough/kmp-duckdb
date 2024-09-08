package org.danbrough.duckdb

import kotlinx.coroutines.runBlocking
import org.danbrough.xtras.support.getEnv

private val log = klog.logger("simple")

fun demo1() {
  log.info { "demo1" }
  runBlocking {
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


