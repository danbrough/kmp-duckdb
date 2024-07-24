import org.danbrough.xtras.Xtras
import org.danbrough.xtras.XtrasLibrary
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.presetName
import org.danbrough.xtras.androidLibDir
import org.danbrough.xtras.supportsJNI
import org.danbrough.xtras.pathOf
import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.logInfo
import org.danbrough.xtras.capitalized
import org.danbrough.xtras.environmentKonan
import org.danbrough.xtras.environmentNDK
import org.danbrough.xtras.hostTriplet
import org.danbrough.xtras.konanDir
import org.danbrough.xtras.registerXtrasGitLibrary
import org.danbrough.xtras.resolveAll
import org.danbrough.xtras.xtrasExtension
import org.danbrough.xtras.xtrasTesting

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.org.jetbrains.dokka)
  `maven-publish`
}


@Suppress("PropertyName")
val JAVA_VERSION =JavaVersion.VERSION_11
@Suppress("PropertyName")
val JVM_TARGET = JvmTarget.JVM_11


/*
java {
  sourceCompatibility = JAVA_VERSION
  targetCompatibility = JAVA_VERSION
}
*/



val KonanTarget.duckdbBinDir: File
  get() = when (this) {
    KonanTarget.LINUX_X64 -> file("../bin/amd64")
    KonanTarget.LINUX_ARM64 -> file("../bin/aarch64")
    KonanTarget.MINGW_X64 -> file("../bin/windows")
    KonanTarget.ANDROID_X64, KonanTarget.ANDROID_ARM64, KonanTarget.ANDROID_ARM32 -> file("../bin/android/$androidLibDir")
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
  //macosX64()
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
  //androidNativeArm32()

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
          if (konanTarget == HostManager.host && buildType == NativeBuildType.DEBUG) {
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

val xtras = xtrasExtension.apply {
  javaVersion = JavaVersion.VERSION_11
  androidConfig {
    minSDKVersion = 24
  }
}

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
    sourceCompatibility = xtras.javaVersion
    targetCompatibility = xtras.javaVersion
  }
}


registerXtrasGitLibrary<XtrasLibrary>("duckdb") {
  environment { target ->
    //put("CFLAGS", "-Wno-unused-command-line-argument -Wno-macro-redefined")
    if (target != null) {
      if (target.family == Family.ANDROID)
        environmentNDK(xtras, target, project)
      else if (target.family == Family.LINUX)
        environmentKonan(this@registerXtrasGitLibrary, target, project)
    }
  }

  buildCommand { target ->
    val compileDir = sourceDir(target).resolveAll("build", target.name).absolutePath
    writer.println(
      """
        |DUCKDB_EXTENSIONS="icu;json;parquet"
        |#DISABLE_PARQUET=1
        |mkdir -p "$compileDir"
        |cd "$compileDir"
      """.trimMargin()
    )

    if (target.family == Family.ANDROID) {
      writer.println(
        """
          |ANDROID_ABI=${target.androidLibDir}
          |ANDROID_PLATFORM=24
          |PLATFORM_NAME="android_${'$'}ANDROID_ABI"
      """.trimMargin()
      )
    } else {
      writer.println(
        """
          |PLATFORM_NAME=${target.name}
      """.trimMargin()
      )
    }

    writer.println(
      """
      |cmake -G "Ninja" -DEXTENSION_STATIC_BUILD=1 \
      |-DBUILD_EXTENSIONS=${'$'}DUCKDB_EXTENSIONS \
      |-DCMAKE_VERBOSE_MAKEFILE=on \
      |-DLOCAL_EXTENSION_REPO=""  -DOVERRIDE_GIT_DESCRIBE="" \
      |-DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
      |-DCMAKE_CXX_COMPILER="clang++" \
      |-DCMAKE_C_COMPILER="clang" \
      |-DDUCKDB_EXPLICIT_PLATFORM=${'$'}PLATFORM_NAME \
      """.trimMargin()
    )

    if (target.family == Family.ANDROID) {
      writer.println(
        """
          |-DANDROID_PLATFORM=${'$'}ANDROID_PLATFORM \
          |-DANDROID_ABI=${'$'}ANDROID_ABI -DCMAKE_TOOLCHAIN_FILE=${'$'}ANDROID_NDK/build/cmake/android.toolchain.cmake \
          |-DDUCKDB_EXTRA_LINK_FLAGS="-llog" \""".trimMargin()
      )
    }

    if (target == KonanTarget.LINUX_ARM64){
      target.hostTriplet
      writer.println("""
        |-DCMAKE_RANLIB="ranlib"  -DCMAKE_AR="llvm-ar" \
        |-DCMAKE_C_COMPILER_TARGET=${target.hostTriplet} \
        |-DCMAKE_CXX_COMPILER_TARGET=${target.hostTriplet} \
        |-DCMAKE_C_COMPILER_EXTERNAL_TOOLCHAIN=${konanDir}/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
        |-DCMAKE_CXX_COMPILER_EXTERNAL_TOOLCHAIN=${konanDir}/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
        |-DCMAKE_SYSROOT=${konanDir}/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot \
      """.trimMargin())
/*
cmake -G "Ninja" -DFORCE_COLORED_OUTPUT=1   \
-DCMAKE_SYSTEM_NAME=Linux \
-DCMAKE_SYSTEM_PROCESSOR=aarch64 \
  -DCMAKE_CROSSCOMPILING=TRUE \
    -DEXTENSION_STATIC_BUILD=1 \
   -DCMAKE_RANLIB="ranlib"  -DCMAKE_AR="llvm-ar" \
   -DCMAKE_VERBOSE_MAKEFILE=off \
   -DBUILD_EXTENSIONS=$DUCKDB_EXTENSIONS \
   -DDUCKDB_EXPLICIT_PLATFORM=$PLATFORM_NAME -DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
   -DCMAKE_CXX_COMPILER="clang++" \
   -DCMAKE_C_COMPILER="clang" \
  -DCMAKE_C_COMPILER_TARGET=$TARGET \
  -DCMAKE_CXX_COMPILER_TARGET=$TARGET \
  -DCMAKE_C_COMPILER_EXTERNAL_TOOLCHAIN=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
  -DCMAKE_CXX_COMPILER_EXTERNAL_TOOLCHAIN=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
  -DCMAKE_SYSROOT=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot \
    -DOVERRIDE_GIT_DESCRIBE="" \
   -DCMAKE_BUILD_TYPE=Release ../..
 */
    }

    writer.println(
      """
        |-DCMAKE_BUILD_TYPE=Release ../.. || exit 1
        |cmake --build . --config Release || exit 1
    """.trimMargin()
    )

    if (target.family == Family.ANDROID){
      //strip them as they are huge
      writer.println("""
        |llvm-strip duckdb src/libduckdb.so
      """.trimMargin())
    }

    writer.println(
      """
        |mkdir -p "${buildDir(target).absolutePath}/bin" "${buildDir(target).absolutePath}/lib" "${
        buildDir(
          target
        ).absolutePath
      }/include"
        |cp duckdb "${buildDir(target).absolutePath}/bin" 
        |cp src/libduckdb.so "${buildDir(target).absolutePath}/lib"
        |cp ../../src/include/duckdb.h ../../src/include/duckdb.hpp "${buildDir(target).absolutePath}/include"
    """.trimMargin()
    )


  }
}

/*
export PATH="$LLVM_DIR:/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin:$PATH"

#export TOOLCHAIN_PREFIX=aarch64-unknown-linux-gnu-
TARGET=aarch64-unknown-linux-gnu

cmake -G "Ninja" -DFORCE_COLORED_OUTPUT=1   \
-DCMAKE_SYSTEM_NAME=Linux \
-DCMAKE_SYSTEM_PROCESSOR=aarch64 \
  -DCMAKE_CROSSCOMPILING=TRUE \
    -DEXTENSION_STATIC_BUILD=1 \
   -DCMAKE_RANLIB="ranlib"  -DCMAKE_AR="llvm-ar" \
   -DCMAKE_VERBOSE_MAKEFILE=off \
   -DBUILD_EXTENSIONS=$DUCKDB_EXTENSIONS \
   -DDUCKDB_EXPLICIT_PLATFORM=$PLATFORM_NAME -DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
   -DCMAKE_CXX_COMPILER="clang++" \
   -DCMAKE_C_COMPILER="clang" \
  -DCMAKE_C_COMPILER_TARGET=$TARGET \
  -DCMAKE_CXX_COMPILER_TARGET=$TARGET \
  -DCMAKE_C_COMPILER_EXTERNAL_TOOLCHAIN=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
  -DCMAKE_CXX_COMPILER_EXTERNAL_TOOLCHAIN=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
  -DCMAKE_SYSROOT=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot \
    -DOVERRIDE_GIT_DESCRIBE="" \
   -DCMAKE_BUILD_TYPE=Release ../..
 */