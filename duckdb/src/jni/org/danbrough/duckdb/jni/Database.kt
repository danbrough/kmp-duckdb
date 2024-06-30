@file:OptIn(ExperimentalNativeApi::class)
@file:Suppress("FunctionName")

package org.danbrough.duckdb.jni

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops._duckdb_database
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_database
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_open_ext
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jclass
import org.danbrough.xtras.jni.jlong
import org.danbrough.xtras.jni.jobject
import org.danbrough.xtras.jni.jstring
import kotlin.experimental.ExperimentalNativeApi

private const val JNI_PREFIX = "Java_org_danbrough_duckdb_Database"


@CName("${JNI_PREFIX}_create")
fun databaseCreate(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  jPath: jstring?,
  jConfig: jobject?
): jlong {
  log.trace { "databaseCreate(): ${jPath?.toKString(env)} " }
  memScoped {
    val dbHandle: duckdb_databaseVar = alloc()
    val error: CPointerVarOf<CPointer<ByteVar>> = alloc()
    //config?.handle?.value

    if (duckdb_open_ext(jPath.toKString(env), dbHandle.ptr, null, error.ptr) == DuckDBError) {
      error("duckdb_open_ext failed: ${error.value?.toKString()}")
    }

    return dbHandle.value.toLong()
  }

}

@CName("${JNI_PREFIX}_destroy")
fun databaseDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, handle: jlong) {
  log.warn { "databaseDestroy(): handle: $handle" }
  val db: duckdb_database? = handle.toCPointer()

}