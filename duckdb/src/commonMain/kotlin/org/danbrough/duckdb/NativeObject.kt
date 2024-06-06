package org.danbrough.duckdb

import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope

@Suppress("MemberVisibilityCanBePrivate")
@OptIn(ExperimentalForeignApi::class)
abstract class NativeObject<T : CPointerVarOf<*>>(val memScope: MemScope) : AutoCloseable {

	abstract val handle: T
}