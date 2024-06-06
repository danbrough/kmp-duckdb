import org.danbrough.xtras.capitalized
import org.danbrough.xtras.decapitalized
import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.kotlinTargetName
import org.danbrough.xtras.xtrasTesting
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	id("org.danbrough.xtras")
}

val duckDBVersion = project.property("duckdb.version").toString()
version = duckDBVersion

val KonanTarget.duckdbBinDir: File
	get() = when (this) {
		KonanTarget.LINUX_X64 -> file("../bin/amd64/")
		KonanTarget.LINUX_ARM64 -> file("../bin/aarch64/")
		KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> file("../bin/darwin")
		else -> TODO("Handle target: $this")
	}


val interopsDefFile = file("src/cinterops/duckdb.def")
val generateDefFileTaskName = "generateInteropsDefFile"

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

		compilations["main"].cinterops {
			create("duckdb") {
				definitionFile = interopsDefFile
				tasks.getByName(interopProcessingTaskName).dependsOn(generateDefFileTaskName)
			}
		}

		binaries {
			executable("demo1") {
				entryPoint = "org.danbrough.duckdb.main"
				runTask?.apply {
					environment(HostManager.host.envLibraryPathName, konanTarget.duckdbBinDir)
				}
			}
		}
	}
}



xtrasTesting {
}

afterEvaluate {
	tasks.register(generateDefFileTaskName) {
		val headersFile = file("src/cinterops/duckdb_headers.def")
		val codeFile = file("src/cinterops/duckdb_code.h")
		inputs.files(headersFile,codeFile)
		outputs.files(interopsDefFile)

		actions.add {
			headersFile.copyTo(interopsDefFile, overwrite = true)
		}
		actions.add {
			kotlin.targets.withType<KotlinNativeTarget>().forEach { target ->
				val binDir = target.konanTarget.duckdbBinDir
				/*
				linkerOpts.linux_x64 = -L/home/dan/workspace/duckdb/kmp/bin/amd64
compilerOpts.linux_x64 =  -I/home/dan/workspace/duckdb/kmp/bin/amd64
libraryPaths.linux_x64  = /home/dan/workspace/duckdb/kmp/bin/amd64
				 */
				interopsDefFile.appendText(
					"""
						linkerOpts.${target.konanTarget.name} = -L${binDir.absolutePath}
						compilerOpts.${target.konanTarget.name} = -I${binDir.absolutePath}
						libraryPaths.${target.konanTarget.name} = ${binDir.absolutePath}
						
						
					""".trimIndent()
				)
			}
		}

		actions.add{
			interopsDefFile.appendText("---\n${codeFile.readText()}\n")
		}

	}
}


