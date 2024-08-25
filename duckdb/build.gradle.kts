@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.danbrough.duckdb.duckdb
import org.danbrough.xtras.capitalized
import org.danbrough.xtras.logTrace
import org.danbrough.xtras.supportsJNI
import org.danbrough.xtras.xtrasAndroidConfig
import org.danbrough.xtras.xtrasTesting
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.org.jetbrains.dokka)
  alias(libs.plugins.duckdb)
  `maven-publish`
}


java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}


class Demo(val name: String, val entryPoint: String, vararg args: String) {
  var description: String = "$name test application"
  val cmdArgs: Array<out String> = args
}

val demos = listOf(
  Demo("demo1", "org.danbrough.duckdb.demo1", "-d", file("test.db").absolutePath),
  Demo("demo2", "org.danbrough.duckdb.demo2"),
  Demo("demo3", "org.danbrough.duckdb.demo3"),
  Demo("demo4", "org.danbrough.duckdb.demo4"),
  Demo("demo5", "org.danbrough.duckdb.demo5"),
  Demo("demoVectors", "org.danbrough.duckdb.demoVectors"),
)

duckdb {
  buildEnabled = false
}

kotlin {

  jvm()

  linuxX64()
  linuxArm64()

  if (HostManager.hostIsMac) {
    macosX64()
    macosArm64()
  } else {
    androidNativeX64()
    androidNativeArm64()
    androidNativeArm32()
  }

  androidTarget {
    publishLibraryVariants("release")
  }

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions {
    listOf("kotlinx.cinterop.ExperimentalForeignApi").also { optIn = it }
    freeCompilerArgs = freeCompilerArgs.get() + listOf("-Xexpect-actual-classes")
  }

  sourceSets {
    all {
      languageSettings.optIn("kotlin.ExperimentalStdlibApi")
    }
  }

  val commonMain by sourceSets.getting {
    dependencies {
      implementation(libs.klog.core)
      implementation(libs.xtras.support)
      api(project(":libs"))
    }
  }

  val commonTest by sourceSets.getting {
    dependencies {
      implementation(kotlin("test"))
      implementation(libs.kotlinx.coroutines)
      implementation(libs.kotlinx.datetime)
    }
  }

  /**
   * Shared native code
   */
  val posixMain by sourceSets.creating {
    dependsOn(commonMain)
  }

  val posixTest by sourceSets.creating {
    dependsOn(commonTest)
  }

  /**
   * Shared JVM and Android code
   */
  val jvmAndroidMain by sourceSets.creating {
    dependsOn(commonMain)
  }

  val jvmMain by sourceSets.getting {
    dependsOn(jvmAndroidMain)
  }

  val androidMain by sourceSets.getting {
    dependsOn(jvmAndroidMain)
  }

  targets.withType<KotlinNativeTarget> {

    compilations["main"].apply {
      defaultSourceSet.dependsOn(posixMain)
      if (konanTarget.supportsJNI) {
        defaultSourceSet.kotlin.srcDir("src/jni")
        dependencies {
          implementation(libs.xtras.jni)
        }
      }
    }

    binaries {
      if (konanTarget.supportsJNI) {
        sharedLib("duckdbkt") {
          // copyToJniLibs()
        }
      }

      demos.forEach { demoInfo ->
        executable(demoInfo.name, buildTypes = setOf(NativeBuildType.DEBUG)) {

          entryPoint = demoInfo.entryPoint
          compilation = compilations["test"]

          if (konanTarget == HostManager.host)
            tasks.create("run${demoInfo.name.capitalized()}") {
              description = demoInfo.description
              group = "run"
            }.dependsOn(runTaskName)
        }
      }
    }
  }
}


xtrasTesting {}


//xtrasAndroidConfig(namespace = "org.danbrough.duckdb") {
xtrasAndroidConfig {
  defaultConfig {
    ndk {
      abiFilters += setOf("arm64-v8a", "x86_64", "armeabi-v7a")
    }
  }

  sourceSets.all {
    jniLibs {
      logTrace("JNILIBS:$name ${directories.joinToString()}")
    }
  }
}


tasks.getByName("runDemo1DebugExecutableLinuxX64")
