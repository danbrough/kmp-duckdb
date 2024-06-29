package org.danbrough.duckdb


val log = klog.logger("DuckDB")

fun <R> duckdb(path: String?, config: DatabaseConfig? = null, block: Database.() -> R) =
  Database(path, config).use(block)
