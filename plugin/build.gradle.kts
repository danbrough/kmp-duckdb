import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  `maven-publish`
  signing
  id("org.jetbrains.dokka") version "1.9.20"
  id("org.danbrough.xtras")
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
  //noinspection UseTomlInstead
  implementation("org.danbrough.xtras:plugin:0.0.1-beta10")
  //noinspection UseTomlInstead
  compileOnly("com.android.tools.build:gradle:8.5.2")
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
