@file:Suppress("NOTHING_TO_INLINE")

package org.danbrough.duckdb

import org.danbrough.duckdb.DatabaseConfig.Companion.destroy

actual class PreparedStatement : NativePeer(), NativePreparedStatement {

  internal companion object {
    @JvmStatic
    external fun create(): Long

    @JvmStatic
    external fun destroy(handle:Long)
  }

  actual val connection: Connection
    get() = TODO("Not yet implemented")

  actual val sql: String
    get() = TODO("Not yet implemented")

  actual fun clearBindings() {
  }

  actual inline fun <T : Any> bind(
    index: Int,
    value: T
  ): PreparedStatement {
    TODO("Not yet implemented")
  }

  actual inline fun bindNull(index: Int): PreparedStatement {
    TODO("Not yet implemented")
  }

  actual fun <R> execute(block: Result.() -> R): R {
    TODO("Not yet implemented")
  }

  override val handle: Long = create()


  override fun nativeDestroy() = destroy(handle)

}

actual interface NativePreparedStatement : AutoCloseable