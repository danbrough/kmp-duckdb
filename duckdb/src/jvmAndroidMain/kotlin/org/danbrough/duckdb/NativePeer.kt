package org.danbrough.duckdb

/**
 * Base class for objects that encapsulate a native object
 */

abstract class NativePeer : LibrarySupport(), AutoCloseable {

  abstract var handle: Long

  /**
   * Destroy the native peer
   */
  protected abstract fun nativeDestroy()

  final override fun close() {
    if (handle != 0L) {
      synchronized(this) {
        if (handle != 0L) {
          onClose()
          nativeDestroy()
          handle = 0L
        }
      }
    }
  }

  /**
   * Perform additional cleanup before the handle is destroyed
   */
  protected open fun onClose() {}

  protected fun finalize() {
    if (handle != 0L) log.error { "$this: finalize: handle: $handle" }
    close()
  }
}