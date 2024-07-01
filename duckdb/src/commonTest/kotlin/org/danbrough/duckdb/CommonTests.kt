package org.danbrough.duckdb

import org.danbrough.xtras.support.getEnv
import kotlin.test.Test

class CommonTests {
  @Test
  fun test1() {
    duckdb {
      log.info { "database: $this" }
      connect {
        log.info { "connected: $this" }

        query("SELECT NULL::VARCHAR") {
          log.debug { "got query: $this" }
          log.info { get<String?>(0, 0) }
        }
      }
    }
  }


  @Test
  fun test2() {
    duckdb {
      log.info { "database: $this" }
      connect {
        log.info { "connected: $this" }
        query("SELECT current_timestamp::VARCHAR") {
          log.debug { "got query: $this" }
          log.info { get<String>(0, 0) }
        }
        /*        query("SELECT NULL::VARCHAR") {
                  log.debug { "got query: $this" }
                  log.info { get<String?>(0, 0) }
                }*/
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

  @Test
  fun testDemo4() {
    demo4()
  }
}