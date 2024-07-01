@file:OptIn(ExperimentalNativeApi::class)
@file:Suppress("FunctionName")

package org.danbrough.duckdb.jni

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_column_count
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_destroy_result
import org.danbrough.duckdb.cinterops.duckdb_free
import org.danbrough.duckdb.cinterops.duckdb_open_ext
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.cinterops.duckdb_row_count
import org.danbrough.duckdb.cinterops.duckdb_value_int32
import org.danbrough.duckdb.cinterops.duckdb_value_varchar
import org.danbrough.duckdb.cinterops.duckdb_value_varchar_internal
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jclass
import org.danbrough.xtras.jni.jint
import org.danbrough.xtras.jni.jlong
import org.danbrough.xtras.jni.jobject
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
  row: ULong,
  col: ULong
): jstring? {
  log.warn { "resultGetStringValue(): $handle: $row:$col" }
  val p: CPointer<duckdb_result> = handle.toCPointer()!!
  val str = duckdb_value_varchar(p, col.toULong(), row.toULong())
  log.trace { "got string: ${str?.toKString()}" }
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


@CName("${JNI_PREFIX}_getInt32")
fun resultGetInt32(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  row: jlong,
  col: jlong
): jint = duckdb_value_int32(handle.toCPointer()!!, col.toULong(), row.toULong())


/*




@OptIn(ExperimentalStdlibApi::class)
@CName("${JNI_PREFIX}_destroy")
fun databaseDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, handle: jlong) {
  log.warn { "databaseDestroy(): handle: ${handle.toHexString()}" }
  val db: CPointer<duckdb_databaseVar> = handle.toCPointer()!!
  log.debug { "got handle: $db: ${db.toLong().toHexString()}" }
  duckdb_close(db)
  nativeHeap.free(db)
}

@OptIn(ExperimentalStdlibApi::class)
@CName("${JNI_PREFIX}_test")
fun databaseTest(env: CPointer<JNIEnvVar>, clazz: jclass) {
  log.info { "databaseTest()" }

  val dbHandle: duckdb_databaseVar = nativeHeap.alloc()
  val error: CPointerVarOf<CPointer<ByteVar>> = nativeHeap.alloc()
  //config?.handle?.value

  if (duckdb_open_ext(null, dbHandle.ptr, null, error.ptr) == DuckDBError) {
    error("duckdb_open_ext failed: ${error.value?.toKString()}")
  }

  val l = dbHandle.ptr.toLong()

  val p: CPointer<duckdb_databaseVar> = l.toCPointer()!!
  log.debug { "got pointer: $p" }
  duckdb_close(p)
  nativeHeap.free(dbHandle.ptr)
}

*/
