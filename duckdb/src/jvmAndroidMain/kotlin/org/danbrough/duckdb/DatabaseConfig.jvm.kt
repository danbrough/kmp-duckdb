package org.danbrough.duckdb

actual interface DatabaseConfigPeer : AutoCloseable

actual class DatabaseConfig : NativePeer(), DatabaseConfigPeer {


  companion object {

    @JvmStatic
    external fun create(): Long

    @JvmStatic
    external fun destroy(handle: Long)

    @JvmStatic
    external fun setOption(handle: Long, name: String, value: String)
  }

  override fun nativeCreate(): Long = create()

  override fun nativeDestroy(ref: Long) = destroy(ref)


  actual operator fun set(name: String, option: String) =
    setOption(handle, name, option)



}

