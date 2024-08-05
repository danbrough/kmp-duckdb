package org.danbrough.duckdb

expect interface DataChunkPeer : AutoCloseable

expect class DataChunk : DataChunkPeer {
  override fun close()
}