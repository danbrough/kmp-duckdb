@file:OptIn(ExperimentalNativeApi::class)
@file:Suppress("FunctionName")

package org.danbrough.duckdb.jni

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jclass
import org.danbrough.xtras.jni.jlong
import org.danbrough.xtras.jni.jstring
import kotlin.experimental.ExperimentalNativeApi

private const val JNI_PREFIX = "Java_org_danbrough_duckdb_jni_Database"

@CName("${JNI_PREFIX}_test")
fun databaseTest(env: CPointer<JNIEnvVar>, clazz: jclass) {

  //val path = env.pointed.pointed?.GetStringUTFChars!!(env,jPath,null)

  log.trace { "databaseTest(): path" }
}


internal fun <R> jstring?.useStringCharsUTF(
  env: CPointer<JNIEnvVar>,
  block: (CPointer<ByteVar>?) -> R
): R {
  val str = if (this != null) env.pointed.pointed!!.GetStringUTFChars?.invoke(
    env,
    this,
    null
  ) else null
  return block(str).apply {
    if (this != null)
      env.pointed.pointed!!.ReleaseStringUTFChars!!.invoke(env, this@useStringCharsUTF, str)
  }
}


@CName("${JNI_PREFIX}_create")
fun databaseCreate(env: CPointer<JNIEnvVar>, clazz: jclass, jPath: jstring?): jlong {
  return jPath.useStringCharsUTF(env) { path ->
    log.trace { "databaseCreate(): ${path?.toKString()}" }
    1L
  }
}

@CName("${JNI_PREFIX}_destroy")
fun databaseDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, handle: jlong) {
  log.warn { "databaseDestroy(): handle: $handle" }
}