import org.danbrough.duckdb.duckdb
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.duckdb)
}

duckdb {
  buildEnabled = false
}

kotlin {

  linuxX64()
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
    binaries {
      executable("demo1") {
        entryPoint = "org.danbrough.duckdb.demo1"
      }
    }
  }
}