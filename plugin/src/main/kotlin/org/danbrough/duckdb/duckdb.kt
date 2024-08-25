package org.danbrough.duckdb

import org.danbrough.xtras.XtrasLibrary
import org.danbrough.xtras.androidLibDir
import org.danbrough.xtras.environmentKonan
import org.danbrough.xtras.environmentNDK
import org.danbrough.xtras.hostTriplet
import org.danbrough.xtras.konanDir
import org.danbrough.xtras.registerXtrasGitLibrary
import org.danbrough.xtras.resolveAll
import org.danbrough.xtras.sharedLibExtn
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.utils.`is`
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget


fun Project.duckdb(libName: String = "duckdb", block: XtrasLibrary.() -> Unit = {}) =

  registerXtrasGitLibrary<XtrasLibrary>(libName) {

    cinterops {
      declaration = """
        package = org.danbrough.duckdb.cinterops
        headers = duckdb.h
        linkerOpts = -lduckdb
      """.trimIndent()
    }

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
        writer.println("PLATFORM_NAME=${target.name}")
        if (target == KonanTarget.MACOS_X64)
          writer.println("export CFLAGS=\"-isysroot /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX14.sdk\"")
        else if (target == KonanTarget.MACOS_ARM64)
          writer.println("export CFLAGS=\"-isysroot /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX14.sdk\" -arch arm64")
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
      } else if (target == KonanTarget.LINUX_ARM64) {
        target.hostTriplet
        writer.println(
          """
        |-DCMAKE_RANLIB="ranlib"  -DCMAKE_AR="llvm-ar" \
        |-DCMAKE_C_COMPILER_TARGET=${target.hostTriplet} \
        |-DCMAKE_CXX_COMPILER_TARGET=${target.hostTriplet} \
        |-DCMAKE_C_COMPILER_EXTERNAL_TOOLCHAIN=${konanDir}/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
        |-DCMAKE_CXX_COMPILER_EXTERNAL_TOOLCHAIN=${konanDir}/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
        |-DCMAKE_SYSROOT=${konanDir}/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot \
      """.trimMargin()
        )
      } else if (target == KonanTarget.MACOS_X64) {
        writer.println("-DCMAKE_SYSTEM_NAME=Darwin \\")
        writer.println("-DCMAKE_SYSTEM_PROCESSOR=x64 \\")
        writer.println("-DCMAKE_CROSSCOMPILING=FALSE \\")
      }else if (target == KonanTarget.MACOS_ARM64) {
        writer.println("-DCMAKE_SYSTEM_NAME=Darwin \\")
        writer.println("-DCMAKE_SYSTEM_PROCESSOR=arm64 \\")
        writer.println("-DCMAKE_CROSSCOMPILING=TRUE \\")
      }

      writer.println(
        """
        |-DCMAKE_BUILD_TYPE=Release ../.. || exit 1
        |cmake --build . --config Release || exit 1
    """.trimMargin()
      )

      if (target.family == Family.ANDROID) {
        //strip them as they are huge
        writer.println(
          """
        |${'$'}ANDROID_NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-strip duckdb src/libduckdb.so
      """.trimMargin()
        )
      }

      writer.println(
        """
        |mkdir -p "${buildDir(target).absolutePath}/bin" "${buildDir(target).absolutePath}/lib" "${
          buildDir(
            target
          ).absolutePath
        }/include"
        |cp duckdb "${buildDir(target).absolutePath}/bin" 
        |cp src/libduckdb.${target.sharedLibExtn} "${buildDir(target).absolutePath}/lib"
        |cp ../../src/include/duckdb.h ../../src/include/duckdb.hpp "${buildDir(target).absolutePath}/include"
    """.trimMargin()
      )


    }

    block()
  }


