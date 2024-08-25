import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  `maven-publish`
  signing
  id("org.jetbrains.dokka") version "1.9.20"
  id("org.danbrough.xtras") version "0.0.1-beta09"
}

repositories {
  maven("https://s01.oss.sonatype.org/content/groups/staging/")
  mavenCentral()
  google()
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
//  withJavadocJar()
}


dependencies {
  compileOnly(kotlin("gradle-plugin"))
  implementation(libs.xtras.plugin)
  compileOnly(libs.android.gradle)

}

gradlePlugin {
  plugins {
    create("duckdb") {
      id = group.toString()
      implementationClass = "$group.DuckDBPlugin"
      displayName = "DuckDB Plugin"
      description = "Kotlin multiplatform support plugin for duckdb"
    }
  }
}
