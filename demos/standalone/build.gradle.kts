import org.danbrough.duckdb.duckdb
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.duckdb)
}

repositories {
  maven("https://s01.oss.sonatype.org/content/groups/staging/")
  mavenCentral()
  gradlePluginPortal()
}

duckdb {
  buildEnabled = false
}

kotlin {
  applyDefaultHierarchyTemplate()


  if (HostManager.hostIsMac) {
    macosX64()
    macosArm64()
  } else {
    linuxX64()
    linuxArm64()
  }

  val commonMain by sourceSets.getting {
    dependencies {
      implementation(libs.klog.core)
      implementation(libs.kotlinx.coroutines)
      implementation(libs.xtras.support)
      implementation(libs.kotlinx.datetime)
      implementation(libs.xtras.duckdb)
//      implementation(project(":duckdb"))
    }
  }

  val ldLibPathName = if (HostManager.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH"

  targets.withType<KotlinNativeTarget> {
    //if (this.konanTarget == HostManager.host) {

      binaries {
        executable("demo1") {
          entryPoint = "org.danbrough.duckdb.demo1"

          runTask?.environment(ldLibPathName,System.getenv(ldLibPathName))
        }

      }
    //}
  }
}