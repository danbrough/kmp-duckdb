package org.danbrough.duckdb

open class LibrarySupport {
  companion object {
    init {
      System.loadLibrary("duckdbkt")

    }
  }
}