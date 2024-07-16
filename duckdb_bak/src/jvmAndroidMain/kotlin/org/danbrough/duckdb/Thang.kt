package org.danbrough.duckdb

class Thang {
  external fun dude()

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      log.info { "hello world!" }
    }
  }
}