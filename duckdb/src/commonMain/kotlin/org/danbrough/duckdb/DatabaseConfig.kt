package org.danbrough.duckdb

expect interface DatabaseConfigPeer : AutoCloseable

expect class DatabaseConfig(
  accessMode: AccessMode = AccessMode.AUTOMATIC,
  options: Map<String, String> = emptyMap()
) : DatabaseConfigPeer {
  enum class AccessMode {
    AUTOMATIC, READ_ONLY, READ_WRITE;
  }

  operator fun set(name: String, option: String)

  var accessMode: AccessMode

  var threads: Int

  override fun close()
}

