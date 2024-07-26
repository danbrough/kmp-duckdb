import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.org.jetbrains.dokka ) apply false
  alias(libs.plugins.android.application) apply false
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.jetbrains.compose) apply false
	alias(libs.plugins.compose.compiler) apply false
	alias(libs.plugins.xtras)
}



tasks.register("thang"){
	doFirst {
		println("running gradle task thang.")
		println("xtras.dir=${findProperty("xtras.dir")}")
	}
}

tasks.register("deps"){
	dependsOn(gradle.includedBuild(rootProject.file("deps").absolutePath).task("build"))
}