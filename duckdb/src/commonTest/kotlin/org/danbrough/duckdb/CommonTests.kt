package org.danbrough.duckdb

import org.danbrough.xtras.support.getEnv
import kotlin.test.Test

class CommonTests {
  @Test
  fun test1() {
    val dbPath = "${getEnv("HOME")}/.habitrack/database"
    log.info { "test1() home:${getEnv("HOME")}" }
    duckdb(dbPath) {
      log.debug { "opened db: $this" }
      connect {
        log.debug { "connected: $this" }
      }
    }

  }


  @Test
  fun test2() {
//    val dbPath = File(System.getProperty("java.io.tmpdir")).resolve("stuff/test.db").absolutePath
//    log.info { "opening db: $dbPath" }


    duckdb {
      log.info { "database: $this" }
      connect {
        log.info { "connected: $this" }
        query("SELECT current_timestamp::VARCHAR") {
          log.debug { "got query: $this" }
          log.info { get(0, 0) }
        }
        query("SELECT NULL::VARCHAR") {
          log.debug { "got query: $this" }
          log.info { get(0, 0) }
        }
      }
    }
  }

  @Test
  fun testDemo1() {
    demo1()
  }

  @Test
  fun testDemo2() {
    demo2()
  }

  @Test
  fun testDemo3() {
    demo3()
  }
}