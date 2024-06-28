package org.danbrough.duckdb

actual interface DatabaseConfigPeer : AutoCloseable

actual class DatabaseConfig actual constructor(
  actual var accessMode: AccessMode,
  options: Map<String, String>
) : DatabaseConfigPeer {
  actual override fun close() {
  }

  actual operator fun set(name: String, option: String) {
  }

  actual enum class AccessMode {
    AUTOMATIC, READ_ONLY, READ_WRITE;
  }


  actual var threads: Int
    get() = TODO("Not yet implemented")
    set(value) {}
}

