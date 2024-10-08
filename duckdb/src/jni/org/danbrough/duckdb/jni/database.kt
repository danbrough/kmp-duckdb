@file:OptIn(ExperimentalNativeApi::class)
@file:Suppress("FunctionName")

package org.danbrough.duckdb.jni

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.DuckDBError
import org.danbrough.duckdb.cinterops.duckdb_close
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_open_ext
import org.danbrough.jni.cinterops.JNIEnvVar
import org.danbrough.jni.cinterops.jclass
import org.danbrough.jni.cinterops.jlong
import org.danbrough.jni.cinterops.jobject
import org.danbrough.jni.cinterops.jstring
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
    val dbHandle: duckdb_databaseVar = nativeHeap.alloc()
    val error: CPointerVarOf<CPointer<ByteVar>> = alloc()
    //config?.handle?.value


    if (duckdb_open_ext(jPath.toKString(env), dbHandle.ptr, null, error.ptr) == DuckDBError) {
      error("duckdb_open_ext failed: ${error.value?.toKString()}")
    }

    return dbHandle.ptr.toLong()
  }
}

@OptIn(ExperimentalStdlibApi::class)
@CName("${JNI_PREFIX}_destroy")
fun databaseDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, handle: jlong) {
  log.warn { "databaseDestroy(): handle: ${handle.toHexString()}" }
  val db: CPointer<duckdb_databaseVar> = handle.toCPointer()!!
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

