package org.danbrough.duckdb


import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import org.danbrough.duckdb.cinterops.duckdb_data_chunkVar

actual interface DataChunkPeer : NativePeer<duckdb_data_chunkVar>, AutoCloseable

actual class DataChunk(result: Result) : DataChunkPeer {

  override val handle: duckdb_data_chunkVar = nativeHeap.alloc<duckdb_data_chunkVar>()

  actual override fun close() {
    log.trace { "DataChunk::close()" }
    nativeHeap.free(handle)
  }
}

