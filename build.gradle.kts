plugins {
	alias(libs.plugins.xtras) apply false
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.kotlin.android) apply false
	
	alias(libs.plugins.org.jetbrains.dokka ) apply false
  alias(libs.plugins.android.application) apply false
	alias(libs.plugins.android.library) apply false

	alias(libs.plugins.jetbrains.compose) apply false
	alias(libs.plugins.compose.compiler) apply false
}

val projectGroup = project.property("project.group").toString()
val projectVersion = project.property("project.version").toString()
val duckDBVersion = project.property("duckdb.version").toString()

allprojects{
	group = projectGroup
	version = projectVersion
}

tasks.register("thang"){
	doFirst {
		println("Hello from thang")
	}
}
