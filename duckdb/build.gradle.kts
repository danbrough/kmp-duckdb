import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.xtrasTesting
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
	alias(libs.plugins.kotlin.multiplatform)
}

val duckDBVersion = project.property("duckdb.version").toString()
version = duckDBVersion

kotlin {
	linuxX64()
	linuxArm64()

	@OptIn(ExperimentalKotlinGradlePluginApi::class)
	compilerOptions {
		listOf("kotlinx.cinterop.ExperimentalForeignApi").also { optIn = it }
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(libs.klog.core)
			}
		}
	}

	targets.withType<KotlinNativeTarget> {
		val binDir = when (konanTarget) {
			KonanTarget.LINUX_X64 -> file("../bin/amd64/")
			KonanTarget.LINUX_ARM64 -> file("../bin/aarch64/")
			KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> file("../bin/darwin")
			else -> TODO("Handle target: $konanTarget")
		}
		compilations["main"].cinterops {
			create("duckdb") {
				definitionFile = file("src/cinterops/duckdb.def")
			}
		}
		binaries {
			executable("demo1") {
				entryPoint = "org.danbrough.duckdb.main"

				runTask?.apply {
					environment(HostManager.host.envLibraryPathName, binDir)
				}
			}
		}
	}
}




xtrasTesting {

}