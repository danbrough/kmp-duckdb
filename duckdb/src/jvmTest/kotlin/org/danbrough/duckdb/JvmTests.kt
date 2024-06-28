package org.danbrough.duckdb

import java.io.File
import java.util.Calendar
import java.util.Date
import kotlin.test.Test

class JvmTests {
  @Test
  fun test1() {
    val db = File(System.getProperty("java.io.tmpdir")).resolve("test.db").absolutePath
    log.info { "opening db: $db" }

  }
}