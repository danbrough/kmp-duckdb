package org.danbrough.duckdb

import java.io.File
import kotlin.test.Test

class JvmTests {
  init {
    System.loadLibrary("duckdbkt")
  }

  @Test
  fun test1() {
    val db = File(System.getProperty("java.io.tmpdir")).resolve("test.db").absolutePath
    log.info { "opening db: $db" }

    val config = databaseConfig {
      setAccessMode(DatabaseConfig.AccessMode.READ_WRITE)
      setThreads(4)
    }

    log.trace { "got config: $config" }
    config.close()


  }
}