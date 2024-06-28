package org.danbrough.duckdb

interface NativePeer : AutoCloseable {
  val handle: Long
}