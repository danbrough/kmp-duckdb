package org.danbrough.duckdb

import java.io.File
import kotlin.test.Test


class JvmTests {
  init {
    System.loadLibrary("duckdbkt")
  }

  @Test
  fun test1() {
//    val dbPath = File(System.getProperty("java.io.tmpdir")).resolve("stuff/test.db").absolutePath
//    log.info { "opening db: $dbPath" }


    duckdb {
      log.info { "database: $this" }
      connect {
        log.info { "connected: $this" }
        query("SELECT current_timestamp::VARCHAR") {
          log.debug { "got query: $this" }
          log.info { get(0UL, 0UL) }
        }
        query("SELECT NULL::VARCHAR") {
          log.debug { "got query: $this" }
          log.info { get(0UL, 0UL) }
        }
      }
    }
  }

  @Test
  fun test2() {
    val b = Long.MIN_VALUE
    log.debug { "b: $b = ${b.toULong()} = ${b.toULong().toLong()}" }

    duckdb {
      connect {
        query("CREATE SEQUENCE seq_id")

        query("select nextval('seq_id'),COLUMNS(*),3   as A  from range(DATE '1992-01-01', DATE '1994-03-01', INTERVAL '1' MONTH)") {

        }
      }
    }


  }

}