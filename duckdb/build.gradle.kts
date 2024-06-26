import org.danbrough.xtras.androidLibDir
import org.danbrough.xtras.capitalized
import org.danbrough.xtras.decapitalized
import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.konanDir
import org.danbrough.xtras.kotlinTargetName
import org.danbrough.xtras.logDebug
import org.danbrough.xtras.logError
import org.danbrough.xtras.logInfo
import org.danbrough.xtras.pathOf
import org.danbrough.xtras.supportsJNI
import org.danbrough.xtras.xtras
import org.danbrough.xtras.xtrasAndroidConfig
import org.danbrough.xtras.xtrasDeclareXtrasRepository
import org.danbrough.xtras.xtrasTesting
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTestRun
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.presetName

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  `maven-publish`

  alias(libs.plugins.xtras)
  alias(libs.plugins.android.library)

  alias(libs.plugins.org.jetbrains.dokka)
}


val KonanTarget.duckdbBinDir: File
  get() = when (this) {
    KonanTarget.LINUX_X64 -> file("../bin/amd64")
    KonanTarget.LINUX_ARM64 -> file("../bin/aarch64")
    KonanTarget.MINGW_X64 -> file("../bin/windows")
    KonanTarget.ANDROID_X64, KonanTarget.ANDROID_ARM64 -> file("../bin/android/$androidLibDir")
    KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> file("../bin/darwin")
    else -> TODO("Handle target: $this")
  }


val interopsDefFile = file("src/cinterops/duckdb.def")
val generateDefFileTaskName = "generateInteropsDefFile"

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

kotlin {
  jvm {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget = JvmTarget.JVM_11
    }
  }
  linuxX64()
  linuxArm64()
  macosX64()
  //mingwX64()

  androidTarget {
    publishLibraryVariants("release")
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget = JvmTarget.JVM_11
    }
  }
  androidNativeX64()
  androidNativeArm64()

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

      cinterops {
        create("duckdb") {
          definitionFile = interopsDefFile
          tasks.getByName(interopProcessingTaskName).dependsOn(generateDefFileTaskName)
          this.compilerOpts("-Wno-return-type")
        }
      }
    }

    binaries {
      if (konanTarget.supportsJNI) {
        sharedLib("duckdbkt") {
          copyToJniLibs()
          if (konanTarget == HostManager.host && buildType == NativeBuildType.DEBUG){
            val libDir = linkTask.outputs.files.first()
            tasks.withType<KotlinJvmTest>{
              dependsOn(linkTask)
              val ldPath = pathOf( environment[HostManager.host.envLibraryPathName],libDir, HostManager.host.duckdbBinDir)
              environment[HostManager.host.envLibraryPathName] = ldPath
              logInfo("$name: ldPath = $ldPath")
            }
          }
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

          runTask?.apply {
            environment(HostManager.host.envLibraryPathName, konanTarget.duckdbBinDir)
            args(*demoInfo.cmdArgs)
          }
        }
      }

    }
  }
}


fun SharedLibrary.copyToJniLibs() {
  if (target.konanTarget.family == Family.ANDROID && buildType == NativeBuildType.RELEASE) {
    val copyName = "${name}${target.konanTarget.presetName.capitalized()}_copyToJniLibs"
    val libsDir = linkTask.outputs.files.first()
    val jniLibsDir =
      project.file("src/androidMain/jniLibs/${target.konanTarget.androidLibDir}")

    project.tasks.register<Copy>(copyName) {
      dependsOn(linkTask)
      from(libsDir)
      into(jniLibsDir)
      doLast {
        logInfo("$name: copied files to $jniLibsDir")
      }
    }

    afterEvaluate {
      tasks.getByName("mergeDebugJniLibFolders").dependsOn(copyName)
      tasks.getByName("mergeReleaseJniLibFolders").dependsOn(copyName)
    }
  }
}





tasks.register(generateDefFileTaskName) {
  dependsOn(TASK_GENERATE_TYPES_ENUM)
  val headersFile = file("src/cinterops/duckdb_headers.def")
  val codeFile = file("src/cinterops/duckdb_code.h")
  inputs.files(headersFile, codeFile)
  outputs.files(interopsDefFile)

  doFirst {
    kotlin.targets.withType<KotlinNativeTarget>() {
      val binDir = konanTarget.duckdbBinDir
      if (!binDir.exists())
        throw GradleException("$binDir not found. Have you run ./download_deps.sh?")
    }
  }

  actions.add {
    headersFile.copyTo(interopsDefFile, overwrite = true)
  }

  actions.add {
    kotlin.targets.withType<KotlinNativeTarget>().forEach { target ->
      val binDir = target.konanTarget.duckdbBinDir
      interopsDefFile.appendText(
        """
						linkerOpts.${target.konanTarget.name} = -L${binDir.absolutePath}
						compilerOpts.${target.konanTarget.name} = -I${binDir.absolutePath}
						libraryPaths.${target.konanTarget.name} = ${binDir.absolutePath}
						
						
					""".trimIndent()
      )
    }
  }

  actions.add {
    interopsDefFile.appendText("---\n${codeFile.readText()}\n")
  }
}

tasks.withType<Jar> {
  dependsOn(TASK_GENERATE_TYPES_ENUM)
}

tasks.withType<KotlinCompile> {
  dependsOn(TASK_GENERATE_TYPES_ENUM)
}

tasks.withType<KotlinCompileCommon> {
  dependsOn(TASK_GENERATE_TYPES_ENUM)
}


xtras {
  javaVersion = JavaVersion.VERSION_11
  androidConfig {
    minSDKVersion = 24
  }
}

xtrasDeclareXtrasRepository()

xtrasTesting {}


android {
  namespace = project.group.toString()
  compileSdk = xtras.androidConfig.compileSDKVersion

  defaultConfig {
    minSdk = xtras.androidConfig.minSDKVersion
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    ndk {
      //abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86_64")
      abiFilters += setOf("arm64-v8a", "x86_64")
    }
  }

  compileOptions {
    sourceCompatibility = project.xtras.javaVersion
    targetCompatibility = project.xtras.javaVersion
  }
}
/*

afterEvaluate {
  tasks.withType<KotlinJvmTest> {
    val ldPath =
      pathOf(environment[HostManager.host.envLibraryPathName], HostManager.host.duckdbBinDir)
    environment(HostManager.host.envLibraryPathName, ldPath)
  }

}
*/
