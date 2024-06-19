package org.danbrough.duckdb


val log = klog.logger("DUCKDB")

expect class RootScope

expect fun RootScope.duckdb(path:String?): Database

fun <R> RootScope.duckdb(path: String?, block: Database.() -> R) = duckdb(path).use(block)
