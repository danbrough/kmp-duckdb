package org.danbrough.duckdb

import kotlinx.cinterop.NativePointed
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap

interface NativePeer<T : NativePointed> : AutoCloseable {
  val handle: T
}


