import org.danbrough.xtras.capitalized
import org.danbrough.xtras.decapitalized
import org.danbrough.xtras.envLibraryPathName
import org.danbrough.xtras.kotlinTargetName
import org.danbrough.xtras.logInfo
import org.danbrough.xtras.xtrasDeclareXtrasRepository
import org.danbrough.xtras.xtrasTesting
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	`maven-publish`
	id("org.danbrough.xtras")
}


val KonanTarget.duckdbBinDir: File
	get() = when (this) {
		KonanTarget.LINUX_X64 -> file("../bin/amd64")
		KonanTarget.LINUX_ARM64 -> file("../bin/aarch64")
		KonanTarget.MINGW_X64 -> file("../bin/windows")
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
		"demo", "org.danbrough.duckdb.demo"
	),
	Demo(
		"demo1", "org.danbrough.duckdb.demo1",
		"-d", file("test.db").absolutePath
	),
)

kotlin {
	jvm()
	linuxX64()
	linuxArm64()
	macosX64()
	//mingwX64()

	@OptIn(ExperimentalKotlinGradlePluginApi::class)
	compilerOptions {
		listOf("kotlinx.cinterop.ExperimentalForeignApi").also { optIn = it }
		freeCompilerArgs = freeCompilerArgs.get() + "-Xexpect-actual-classes"
	}


	val commonMain by sourceSets.getting {
		dependencies {
			implementation(libs.klog.core)
			implementation(libs.clikt)
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

	val posixMain by sourceSets.creating {
		dependsOn(commonMain)
	}

	val posixTest by sourceSets.creating {
		dependsOn(commonTest)
	}

	targets.withType<KotlinNativeTarget> {
		compilations["main"].apply {
			defaultSourceSet.dependsOn(posixMain)
			cinterops {
				create("duckdb") {
					definitionFile = interopsDefFile
					tasks.getByName(interopProcessingTaskName).dependsOn(generateDefFileTaskName)
				}
			}
		}

		compilations["test"].apply {
			defaultSourceSet.dependsOn(posixTest)
		}

		binaries {
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



xtrasTesting {
}

xtrasDeclareXtrasRepository()

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

xtrasDeclareXtrasRepository()
