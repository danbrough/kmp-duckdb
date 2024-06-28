plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.xtras) apply false
	alias(libs.plugins.org.jetbrains.dokka ) apply false
	alias(libs.plugins.android.library) apply false
}

val projectGroup = project.property("project.group").toString()
val projectVersion = project.property("project.version").toString()
val duckDBVersion = project.property("duckdb.version").toString()

allprojects{
	group = projectGroup
	version = projectVersion
}

