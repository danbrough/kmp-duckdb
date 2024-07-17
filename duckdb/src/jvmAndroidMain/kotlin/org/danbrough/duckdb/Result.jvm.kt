package org.danbrough.duckdb

actual interface ResultHandle : AutoCloseable


actual class Result(override var handle: Long = 0L) : NativePeer(), ResultHandle {
  companion object {

    @JvmStatic
    external fun destroy(ref: Long)

    @JvmStatic
    external fun getBooleanValue(handle: Long, row: Long, col: Long): Boolean

    @JvmStatic
    external fun getStringValue(handle: Long, row: Long, col: Long): String

    @JvmStatic
    external fun getInt8(handle: Long, row: Long, col: Long): Byte

    @JvmStatic
    external fun getInt16(handle: Long, row: Long, col: Long): Short

    @JvmStatic
    external fun getInt32(handle: Long, row: Long, col: Long): Int

    @JvmStatic
    external fun getUInt8(handle: Long, row: Long, col: Long): Byte

    @JvmStatic
    external fun getUInt16(handle: Long, row: Long, col: Long): Short

    @JvmStatic
    external fun getUInt32(handle: Long, row: Long, col: Long): Int

    @JvmStatic
    external fun getInt64(handle: Long, row: Long, col: Long): Long

    @JvmStatic
    external fun getUInt64(handle: Long, row: Long, col: Long): Long

    @JvmStatic
    external fun getFloat(handle: Long, row: Long, col: Long): Float

    @JvmStatic
    external fun getDouble(handle: Long, row: Long, col: Long): Double

    @JvmStatic
    external fun isNull(handle: Long, row: Long, col: Long): Boolean

    @JvmStatic
    external fun rowCount(handle: Long): Long

    @JvmStatic
    external fun colCount(handle: Long): Long

    @JvmStatic
    external fun rowsChanged(handle: Long): Long
  }

  actual val rowCount: Long = rowCount(handle)

  actual val columnCount: Long = colCount(handle)

  actual val rowsChanged: Long = rowsChanged(handle)

  override fun nativeDestroy() = destroy(handle)

  actual inline fun <reified T : Any?> get(row: Long, col: Long): T =
    when (T::class) {
      Boolean::class -> getBooleanValue(handle, row, col)
      String::class -> getStringValue(handle, row, col)
      Byte::class -> getInt8(handle, row, col)
      Short::class -> getInt16(handle, row, col)
      Int::class -> getInt32(handle, row, col)
      Long::class -> getInt64(handle, row, col)
      UByte::class -> getInt8(handle, row, col).toUByte()
      UShort::class -> getInt16(handle, row, col).toUShort()
      UInt::class -> getInt32(handle, row, col).toUInt()
      ULong::class -> getUInt64(handle, row, col).toULong()
      Float::class -> getFloat(handle,row,col)
      Double::class -> getDouble(handle,row,col)
      else -> error("Invalid type: ${T::class}")
    } as T

  actual fun isNull(row: Long, col: Long) = isNull(handle, row, col)

}