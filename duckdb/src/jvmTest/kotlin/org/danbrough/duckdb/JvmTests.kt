package org.danbrough.duckdb

import java.io.File
import kotlin.test.Test

class JvmTests {
  init {
    System.loadLibrary("duckdbkt")
  }

  @Test
  fun test1() {
    val dbPath = File(System.getProperty("java.io.tmpdir")).resolve("stuff/test.db").absolutePath
    log.info { "opening db: $dbPath" }

    duckdb(dbPath) {
      log.debug { "got db: $this" }
    }
  }
}