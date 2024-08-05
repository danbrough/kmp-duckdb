@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.danbrough.duckdb.TASK_GENERATE_TYPES_ENUM
import org.danbrough.duckdb.duckdb
import org.danbrough.duckdb.generateTypesEnumTask
import org.danbrough.xtras.androidLibDir
import org.danbrough.xtras.capitalized
import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.konanDir
import org.danbrough.xtras.kotlinBinaries
import org.danbrough.xtras.logError
import org.danbrough.xtras.logInfo
import org.danbrough.xtras.logTrace
import org.danbrough.xtras.pathOf
import org.danbrough.xtras.supportsJNI
import org.danbrough.xtras.xtrasAndroidConfig
import org.danbrough.xtras.xtrasExtension
import org.danbrough.xtras.xtrasTesting
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.presetName

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.org.jetbrains.dokka)
  `maven-publish`
}

buildscript {
  dependencies {
    //noinspection UseTomlInstead
    classpath("org.danbrough.duckdb:plugin")
  }
}

duckdb {

}

project.generateTypesEnumTask()

class Demo(val name: String, val entryPoint: String, vararg args: String) {
  var description: String = "$name test application"
  val cmdArgs: Array<out String> = args
}

val demos = listOf(
  Demo("demo2", "org.danbrough.duckdb.demo2"),
  Demo("demo1", "org.danbrough.duckdb.demo1", "-d", file("test.db").absolutePath),
  Demo("demo3", "org.danbrough.duckdb.demo3"),
  Demo("demo4", "org.danbrough.duckdb.demo4"),
  Demo("demo5", "org.danbrough.duckdb.demo5"),
  Demo("demoVectors", "org.danbrough.duckdb.demoVectors"),
)


kotlin {


  jvm()
  linuxX64()
  linuxArm64()
  //macosX64()
  //mingwX64()
  androidNativeX64()
  androidNativeArm64()
  androidNativeArm32()

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
          copyToJniLibs()
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


fun SharedLibrary.copyToJniLibs() {
  if (target.konanTarget.family == Family.ANDROID && buildType == NativeBuildType.RELEASE) {

    val copyTaskName = "${name}${target.konanTarget.presetName.capitalized()}_copyToJniLibs"
    val libsDir = linkTask.outputs.files.first()
    val prepareJNILibsTask = tasks.findByName("prepareJNILibs") ?: tasks.create("prepareJNILibs") {
      outputs.dir(file("src/main/jniLibs"))
    }
    prepareJNILibsTask.dependsOn(copyTaskName)


    val jniLibsDir = project.file("src/main/jniLibs/${target.konanTarget.androidLibDir}")

    project.tasks.register<Copy>(copyTaskName) {
      dependsOn(linkTask)
      from(libsDir)
      into(jniLibsDir)

      outputs.dir(jniLibsDir)
      //   jniLibsTask.dependsOn(this)
      doLast {
        logInfo("$name: copied files to $jniLibsDir")
      }
    }
  }
}

afterEvaluate {
  tasks.getByPath("mergeReleaseJniLibFolders").apply {
    dependsOn("prepareJNILibs")
    //inputs.dir(project.file("src/main/jniLibs/"))
  }
}


tasks.all {
  when (this) {
    is Jar, is KotlinCompile, is KotlinCompileCommon, is KotlinNativeCompile ->
      dependsOn(TASK_GENERATE_TYPES_ENUM)
  }
}


xtras {
  jvmTarget = JvmTarget.JVM_17
  javaVersion = JavaVersion.VERSION_17

  androidConfig {
    minSDKVersion = 24
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
  /*
    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
    }*/
}



tasks.withType<KotlinJvmCompile> {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_17
  }
}