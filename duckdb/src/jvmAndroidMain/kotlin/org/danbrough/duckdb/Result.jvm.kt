package org.danbrough.duckdb

actual interface ResultHandle : AutoCloseable


actual class Result(override val handle: Long) : NativePeer(), ResultHandle {
  companion object {

    @JvmStatic
    external fun destroy(ref: Long)

    @JvmStatic
    external fun getBooleanValue(handle: Long, row: Long, col: Long): Boolean

    @JvmStatic
    external fun getStringValue(handle: Long, row: Long, col: Long): String?

    @JvmStatic
    external fun getInt32(handle: Long, row: Long, col: Long): Int?

    @JvmStatic
    external fun getInt64(handle: Long, row: Long, col: Long): Long?

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
      Int::class -> getInt32(handle, row, col)
      Long::class -> getInt64(handle, row, col)
      /*      Boolean::class -> duckdb_value_boolean(handle.ptr,col,row)
            String::class -> duckdb_value_varchar(handle.ptr,col,row).let {data->
              val s = data?.toKStringFromUtf8()
              duckdb_free(data)
              s
            }
            Byte::class -> duckdb_value_int8(handle.ptr,col,row)
            Short::class -> duckdb_value_int16(handle.ptr,col,row)
            Int::class -> duckdb_value_int32(handle.ptr,col,row)
            Long::class -> duckdb_value_int64(handle.ptr,col,row)
            UByte::class -> duckdb_value_uint8(handle.ptr,col,row)
            UShort::class -> duckdb_value_uint16(handle.ptr,col,row)
            UInt::class -> duckdb_value_uint32(handle.ptr,col,row)
            ULong::class -> duckdb_value_uint64(handle.ptr,col,row)
            Float::class -> duckdb_value_float(handle.ptr,col,row)
            Double::class -> duckdb_value_double(handle.ptr,col,row)*/
      else -> error("Invalid type: ${T::class}")
    } as T

  actual fun isNull(row: Long, col: Long) = isNull(handle, row, col)

}