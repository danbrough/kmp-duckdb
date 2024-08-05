package org.danbrough.duckdb

actual  class DataChunk : DataChunkPeer {
  actual override fun close() {
    TODO("Not yet implemented")
  }
}

actual interface DataChunkPeer : AutoCloseable