import org.danbrough.duckdb.duckdb
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.duckdb)
}

duckdb {
  buildEnabled = false
}

kotlin {
  when(HostManager.host){
    KonanTarget.LINUX_X64,KonanTarget.LINUX_ARM64->{
      linuxX64()
      linuxArm64()
    }
    KonanTarget.MACOS_X64,KonanTarget.MACOS_ARM64 -> {
      macosX64()
      macosArm64()
    }
    else -> error("Unsupported host: ${HostManager.host}")
  }

  macosX64()

  val commonMain by sourceSets.getting {
    dependencies {
      //implementation(kotlin("test"))
      implementation(libs.klog.core)
      implementation(libs.kotlinx.coroutines)
      implementation(libs.xtras.support)
      implementation(libs.kotlinx.datetime)
      implementation(project(":duckdb"))
    }
  }

  targets.withType<KotlinNativeTarget>{
    if (this.konanTarget == HostManager.host) {
      binaries {
        executable("demo1") {
          entryPoint = "org.danbrough.duckdb.demo1"
        }
      }
    }
  }
}