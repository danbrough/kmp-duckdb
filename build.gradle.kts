plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.xtras)
}

val projectGroup = project.property("project.group").toString()
val duckDBVersion = project.property("duckdb.version").toString()

allprojects{
	group = projectGroup
	version = duckDBVersion
}

project.generateTypesEnumTask()