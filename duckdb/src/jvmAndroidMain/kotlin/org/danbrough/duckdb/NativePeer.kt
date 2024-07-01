package org.danbrough.duckdb

/**
 * Base class for objects that encapsulate a native object
 */

abstract class NativePeer : LibrarySupport(), AutoCloseable {

  abstract val handle: Long
  
  /**
   * Destroy the native peer
   */
  protected abstract fun nativeDestroy()

  final override fun close() {
    onClose()
    if (handle != 0L)
      nativeDestroy()
  }

  /**
   * Perform additional cleanup before the handle is destroyed
   */
  protected open fun onClose() {}

  protected fun finalize() {
    close()
  }
}