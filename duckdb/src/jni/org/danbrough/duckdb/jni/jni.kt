package org.danbrough.duckdb.jni

import klog.logger
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jstring

val log = logger("DuckDB.jni")


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

internal fun jstring?.toKString(
  env: CPointer<JNIEnvVar>
): String? {
  val str = if (this != null) env.pointed.pointed!!.GetStringUTFChars?.invoke(
    env,
    this,
    null
  ) else null
  return str?.toKString().apply {
    if (this != null)
      env.pointed.pointed!!.ReleaseStringUTFChars!!.invoke(env, this@toKString, str)
  }
}
