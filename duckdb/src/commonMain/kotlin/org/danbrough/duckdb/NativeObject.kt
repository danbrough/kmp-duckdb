package org.danbrough.duckdb

import kotlinx.cinterop.ExperimentalForeignApi

@Suppress("MemberVisibilityCanBePrivate")
@OptIn(ExperimentalForeignApi::class)
interface NativeObject<T> : AutoCloseable {
	val handle: T
}

