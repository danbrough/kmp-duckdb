@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.danbrough.duckdb.duckdb
import org.danbrough.xtras.androidLibDir
import org.danbrough.xtras.capitalized
import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.konanDir
import org.danbrough.xtras.logError
import org.danbrough.xtras.logInfo
import org.danbrough.xtras.pathOf
import org.danbrough.xtras.supportsJNI
import org.danbrough.xtras.xtrasAndroidConfig
import org.danbrough.xtras.xtrasExtension
import org.danbrough.xtras.xtrasTesting
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
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


project.generateTypesEnumTask()

class Demo(val name: String, val entryPoint: String, vararg args: String) {
  var description: String = "$name test application"
  val cmdArgs: Array<out String> = args
}

val demos = listOf(
  Demo(
    "demo2", "org.danbrough.duckdb.demo2"
  ),
  Demo(
    "demo1", "org.danbrough.duckdb.demo1",
    "-d", file("test.db").absolutePath
  ),
  Demo(
    "demo3", "org.danbrough.duckdb.demo3"
  ),
  Demo(
    "demo4", "org.danbrough.duckdb.demo4"
  ),
)

duckdb {}

kotlin {
  jvm()
  linuxX64()
  linuxArm64()
  //macosX64()
  //mingwX64()
  androidNativeX64()
  androidNativeArm64()

  androidTarget {
    publishLibraryVariants("release")
  }

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions {
    listOf("kotlinx.cinterop.ExperimentalForeignApi").also { optIn = it }
    freeCompilerArgs = freeCompilerArgs.get() + listOf("-Xexpect-actual-classes")
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
          /*          if (konanTarget == HostManager.host && buildType == NativeBuildType.DEBUG) {
                      val libDir = linkTask.outputs.files.first()
                      tasks.withType<KotlinJvmTest> {
                        dependsOn(linkTask)
                        val ldPath = pathOf(
                          environment[HostManager.host.envLibraryPathName],
                          libDir,
                          HostManager.host.duckdbBinDir
                        )
                        environment[HostManager.host.envLibraryPathName] = ldPath
                        logInfo("$name: ldPath = $ldPath")
                      }
                    }*/
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


fun SharedLibrary.copyToJniLibs(jniLibDirProvider: (KotlinNativeTarget.() -> File)? = null) {
  if (target.konanTarget.family == Family.ANDROID && buildType == NativeBuildType.RELEASE) {
    val copyName = "${name}${target.konanTarget.presetName.capitalized()}_copyToJniLibs"
    val libsDir = linkTask.outputs.files.first()
    val jniLibsDir = jniLibDirProvider?.invoke(target)
      ?: project.file("src/androidMain/jniLibs/${target.konanTarget.androidLibDir}")

    project.tasks.register<Copy>(copyName) {
      dependsOn(linkTask)
      from(libsDir)
      into(jniLibsDir)
      doLast {
        logInfo("$name: copied files to $jniLibsDir")
      }
    }

    afterEvaluate {
      tasks.getByName("mergeDebugJniLibFolders").mustRunAfter(copyName)
      tasks.getByName("mergeReleaseJniLibFolders").mustRunAfter(copyName)
    }
  }
}


tasks.all {
  when (this) {
    is Jar, is KotlinCompile, is KotlinCompileCommon, is KotlinNativeCompile ->
      dependsOn(TASK_GENERATE_TYPES_ENUM)
  }
}


xtras {
  jvmTarget = JvmTarget.JVM_11
  javaVersion = JavaVersion.VERSION_11

  androidConfig {
    minSDKVersion = 24
  }
}

xtrasTesting {}


xtrasAndroidConfig(namespace = "org.danbrough.duckdb") {
  defaultConfig {
    ndk {
      abiFilters += setOf("arm64-v8a", "x86_64")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

