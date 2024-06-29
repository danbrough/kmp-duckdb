package org.danbrough.duckdb

abstract class NativePeer : LibrarySupport(), AutoCloseable {


  @Suppress("LeakingThis")
  protected var handle: Long = nativeCreate()

  /**
   * Create native peer and return stable reference to it
   */
  abstract fun nativeCreate(): Long

  /**
   * Destroy the native peer
   */
  abstract fun nativeDestroy(ref: Long)

  final override fun close() {
    onClose()
    if (handle != 0L) {
      nativeDestroy(handle)
      handle = 0L
    }
  }

  protected open fun onClose() {}

  protected fun finalize() {
    close()
  }
}