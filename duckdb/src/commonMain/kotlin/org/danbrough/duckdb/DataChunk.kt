package org.danbrough.duckdb

@OptIn(ExperimentalStdlibApi::class)
expect interface DataChunkHandle : AutoCloseable

expect class DataChunk {
}