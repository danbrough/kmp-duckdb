package org.danbrough.duckdb

object JNITest {
  init {
    System.loadLibrary("duckdbkt")
  }
  external fun test()
}