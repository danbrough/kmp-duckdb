import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  `maven-publish`
  signing
  id("org.jetbrains.dokka") version "1.9.20"
  id("org.danbrough.xtras") version "0.0.1-beta06"
}

repositories {
  maven("https://s01.oss.sonatype.org/content/groups/staging/")
  mavenCentral()
  google()
}

java {
  //sourceCompatibility = JavaVersion.VERSION_1_8
  //targetCompatibility = JavaVersion.VERSION_1_8
  withSourcesJar()
//  withJavadocJar()
}

kotlin {
  compilerOptions {
//    this.jvmTarget = JvmTarget.JVM_1_8
  }
}

dependencies {
  compileOnly(kotlin("gradle-plugin"))
  //noinspection UseTomlInstead
  implementation("org.danbrough.xtras:plugin:0.0.1-beta06")
  //noinspection UseTomlInstead
  compileOnly("com.android.tools.build:gradle:8.5.1")
}

