plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.xtras)
	alias(libs.plugins.org.jetbrains.dokka ) apply false
}

val projectGroup = project.property("project.group").toString()
val projectVersion = project.property("project.version").toString()
val duckDBVersion = project.property("duckdb.version").toString()

allprojects{
	group = projectGroup
	version = projectVersion
}

