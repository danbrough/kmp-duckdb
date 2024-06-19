package org.danbrough.duckdb


interface NativePeer<T> : AutoCloseable {
  val handle: T
}

