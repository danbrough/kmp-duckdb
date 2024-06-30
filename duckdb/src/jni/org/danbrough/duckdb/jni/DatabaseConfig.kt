@file:OptIn(ExperimentalNativeApi::class)

package org.danbrough.duckdb.jni

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.value
import org.danbrough.duckdb.cinterops._duckdb_config
import org.danbrough.duckdb.cinterops.duckdb_config
import org.danbrough.duckdb.cinterops.duckdb_configVar
import org.danbrough.duckdb.cinterops.duckdb_config_count
import org.danbrough.duckdb.cinterops.duckdb_create_config
import org.danbrough.duckdb.cinterops.duckdb_destroy_config
import org.danbrough.duckdb.cinterops.duckdb_get_config_flag
import org.danbrough.duckdb.cinterops.duckdb_set_config
import org.danbrough.duckdb.handleDuckDbError
import org.danbrough.xtras.jni.JNIEnvVar
import org.danbrough.xtras.jni.jclass
import org.danbrough.xtras.jni.jlong
import org.danbrough.xtras.jni.jstring
import platform.posix.size_t
import kotlin.experimental.ExperimentalNativeApi

private const val JNI_PREFIX = "Java_org_danbrough_duckdb_DatabaseConfig"


@CName("${JNI_PREFIX}_create")
fun databaseConfigCreate(env: CPointer<JNIEnvVar>, clazz: jclass, jPath: jstring?): jlong {

  memScoped {
    val handle: duckdb_configVar = alloc()

    duckdb_create_config(handle.ptr).handleDuckDbError {
      "duckdb_create_config failed"
    }

    return handle.value.toLong()
  }
}

//fun duckdbConfigFlags(): Map<String, String> = buildMap {
//  memScoped {
//    val count = duckdb_config_count()
//    org.danbrough.duckdb.log.trace { "duckdb_config_count => $count" }
//    val cName: CPointerVarOf<CPointer<ByteVar>> = alloc()
//    val cDescription: CPointerVarOf<CPointer<ByteVar>> = alloc()
//    var index: size_t = 0.convert()
//    while (index < count) {
//      duckdb_get_config_flag(
//        index++,
//        cName.ptr,
//        cDescription.ptr
//      ).handleDuckDbError { "duckdb_get_config_flag failed" }
//
//      val name = cName.value!!.toKString()
//      val description = cDescription.value!!.toKString()
//      put(name, description)
//      //println("$name:\t$description")
//    }
//  }
//}

@CName("${JNI_PREFIX}_destroy")
fun databaseConfigDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, handle: jlong) {
  log.info { "databaseConfigDestroy(): $handle" }
  val config = handle.toCPointer<_duckdb_config>()
  log.debug { "got config $config" }
  duckdb_destroy_config(config!!.reinterpret())
}


@CName("${JNI_PREFIX}_setOption")
fun databaseConfigSetOption(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  handle: jlong,
  jName: jstring,
  jValue: jstring
) {
  jName.useStringCharsUTF(env) { optionName ->
    jValue.useStringCharsUTF(env) { optionValue ->
      log.debug { "setOption(): ${optionName?.toKString()}:${optionValue?.toKString()}" }
      val config = handle.toCPointer<_duckdb_config>()

      duckdb_set_config(
        config,
        optionName?.toKString(),
        optionValue?.toKString()
      ).handleDuckDbError {
        "duckdb_set_config ${optionName?.toKString()}:${optionValue?.toKString()} failed"
      }
    }
  }
}


