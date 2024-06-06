plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.xtras)
}

val projectGroup = project.property("project.group").toString()


