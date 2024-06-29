package org.danbrough.duckdb

expect interface DatabaseConfigPeer : AutoCloseable

expect class DatabaseConfig() : DatabaseConfigPeer {

  enum class AccessMode {
    AUTOMATIC, READ_ONLY, READ_WRITE;
  }

  operator fun set(name: String, option: String)

  override fun close()
}


fun DatabaseConfig.setAccessMode(accessMode: DatabaseConfig.AccessMode) {
  set("access_mode", accessMode.name)
}

fun DatabaseConfig.setThreads(threads: Int) {
  set("threads", threads.toString())
}

fun databaseConfig(
  accessMode: DatabaseConfig.AccessMode? = null,
  block: DatabaseConfig.() -> Unit = {}
) = DatabaseConfig().apply {
  if (accessMode != null) setAccessMode(accessMode)
  block()
}
