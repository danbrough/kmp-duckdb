@file:OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@file:Suppress("FunctionName")

package org.danbrough.duckdb.jni

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops.duckdb_connect
import org.danbrough.duckdb.cinterops.duckdb_connectionVar
import org.danbrough.duckdb.cinterops.duckdb_databaseVar
import org.danbrough.duckdb.cinterops.duckdb_disconnect
import org.danbrough.duckdb.cinterops.duckdb_query
import org.danbrough.duckdb.cinterops.duckdb_result
import org.danbrough.duckdb.handleDuckDbError
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jclass
import org.danbrough.xtras.jni.jlong
import org.danbrough.xtras.jni.jstring
import kotlin.experimental.ExperimentalNativeApi

private const val JNI_PREFIX = "Java_org_danbrough_duckdb_Connection"


@CName("${JNI_PREFIX}_create")
fun connectionCreate(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  jDatabase: jlong
): jlong {
  log.trace { "connectionCreate(): database:${jDatabase.toHexString()} " }
  memScoped {
    val conn: duckdb_connectionVar = nativeHeap.alloc()
    val db: CPointer<duckdb_databaseVar> = jDatabase.toCPointer()!!
    duckdb_connect(db.pointed.value, conn.ptr).handleDuckDbError {
      "duckdb_connect failed"
    }
    log.trace { "created connection: ${conn.ptr.toLong().toHexString()}" }
    return conn.ptr.toLong()
  }
}


@CName("${JNI_PREFIX}_destroy")
fun connectionDestroy(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  connectionHandle: jlong
) {
  log.trace { "connectionDestroy(): ${connectionHandle.toHexString()}" }
  val p: CPointer<duckdb_connectionVar> = connectionHandle.toCPointer()!!
  duckdb_disconnect(p)
  nativeHeap.free(p)
}


@CName("${JNI_PREFIX}_query")
fun resultQuery(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  connectionHandle: jlong,
  jSql: jstring
): jlong {
  val conn: CPointer<duckdb_connectionVar> = connectionHandle.toCPointer()!!

  val handle: duckdb_result = nativeHeap.alloc<duckdb_result>()
  jSql.useStringCharsUTF(env) { sql ->
    duckdb_query(conn.pointed.value, sql!!.toKString(), handle.ptr).handleDuckDbError {
      "query: $sql"
    }
  }

  return handle.ptr.toLong()
}