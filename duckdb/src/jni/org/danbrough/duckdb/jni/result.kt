@file:OptIn(ExperimentalNativeApi::class)
@file:Suppress("FunctionName")

package org.danbrough.duckdb.jni

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.free
import kotlinx.cinterop.invoke
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_rows_changed
import org.danbrough.duckdb.cinterops.duckdb_value_double
import org.danbrough.duckdb.cinterops.duckdb_value_float
import org.danbrough.duckdb.cinterops.duckdb_value_int16
import org.danbrough.duckdb.cinterops.duckdb_value_int32
import org.danbrough.duckdb.cinterops.duckdb_value_int64
import org.danbrough.duckdb.cinterops.duckdb_value_int8
import org.danbrough.duckdb.cinterops.duckdb_value_uint16
import org.danbrough.duckdb.cinterops.duckdb_value_uint32
import org.danbrough.duckdb.cinterops.duckdb_value_uint64
import org.danbrough.duckdb.cinterops.duckdb_value_uint8
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jbyte
import org.danbrough.xtras.jni.jclass
import org.danbrough.xtras.jni.jdouble
import org.danbrough.xtras.jni.jfloat
import org.danbrough.xtras.jni.jint
import org.danbrough.xtras.jni.jlong
import org.danbrough.xtras.jni.jshort
import org.danbrough.xtras.jni.jstring
import kotlin.experimental.ExperimentalNativeApi

private const val JNI_PREFIX = "Java_org_danbrough_duckdb_Result"

@CName("${JNI_PREFIX}_destroy")
fun resultDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, handle: jlong) {
  log.warn { "resultDestroy(): $handle" }

  val p: CPointer<duckdb_result> = handle.toCPointer()!!
  duckdb_destroy_result(p)
  nativeHeap.free(p)
}

@CName("${JNI_PREFIX}_getStringValue")
fun resultGetStringValue(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: Long,
  col: Long
): jstring? {
  log.debug { "resultGetStringValue(): $handle: $row:$col" }
  val p: CPointer<duckdb_result> = handle.toCPointer()!!
  val str = duckdb_value_varchar(p, col.toULong(), row.toULong())
  log.trace { "got string: ${str?.toKString()}" }
  if (str == null) return null
  val jstr = env.pointed.pointed!!.NewStringUTF!!.invoke(env, str)
  duckdb_free(str)
  return jstr
}

@CName("${JNI_PREFIX}_rowCount")
fun resultRowCount(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong
): jlong = duckdb_row_count(handle.toCPointer()!!).toLong()


@CName("${JNI_PREFIX}_colCount")
fun resultColCount(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong
): jlong = duckdb_column_count(handle.toCPointer()!!).toLong()

@CName("${JNI_PREFIX}_rowsChanged")
fun resultRowsChanged(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong
): jlong = duckdb_rows_changed(handle.toCPointer()!!).toLong()


@CName("${JNI_PREFIX}_getInt8")
fun resultGetInt8(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jbyte = duckdb_value_int8(handle.toCPointer()!!, col.toULong(), row.toULong())

@CName("${JNI_PREFIX}_getInt16")
fun resultGetInt16(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jshort = duckdb_value_int16(handle.toCPointer()!!, col.toULong(), row.toULong())

@CName("${JNI_PREFIX}_getInt32")
fun resultGetInt32(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jint = duckdb_value_int32(handle.toCPointer()!!, col.toULong(), row.toULong())

@CName("${JNI_PREFIX}_getInt64")
fun resultGetInt64(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jlong = duckdb_value_int64(handle.toCPointer()!!, col.toULong(), row.toULong())


@CName("${JNI_PREFIX}_getUInt8")
fun resultGetUInt8(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jbyte = duckdb_value_uint8(handle.toCPointer()!!, col.toULong(), row.toULong()).toByte()

@CName("${JNI_PREFIX}_getUInt16")
fun resultGetUInt16(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jshort = duckdb_value_uint16(handle.toCPointer()!!, col.toULong(), row.toULong()).toShort()

@CName("${JNI_PREFIX}_getUInt32")
fun resultGetIntU32(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jint = duckdb_value_uint32(handle.toCPointer()!!, col.toULong(), row.toULong()).toInt()

@CName("${JNI_PREFIX}_getUInt64")
fun resultGetUInt64(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jlong = duckdb_value_uint64(handle.toCPointer()!!, col.toULong(), row.toULong()).toLong()


@CName("${JNI_PREFIX}_getFloat")
fun resultGetFloat(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jfloat = duckdb_value_float(handle.toCPointer()!!, col.toULong(), row.toULong())


@CName("${JNI_PREFIX}_getDouble")
fun resultGetDouble(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jdouble = duckdb_value_double(handle.toCPointer()!!, col.toULong(), row.toULong())





