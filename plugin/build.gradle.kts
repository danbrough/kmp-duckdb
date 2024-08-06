import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  `maven-publish`
  signing
  id("org.jetbrains.dokka") version "1.9.20"
  id("org.danbrough.xtras") version "0.0.1-beta08"
}

repositories {
  maven("https://s01.oss.sonatype.org/content/groups/staging/")
  mavenCentral()
  google()
}

java {
//  sourceCompatibility = JavaVersion.VERSION_17
//  targetCompatibility = JavaVersion.VERSION_17
  withSourcesJar()
//  withJavadocJar()
}

//kotlin {
//  compilerOptions {
//    jvmTarget = JvmTarget.JVM_17
//  }
//}

dependencies {
  compileOnly(kotlin("gradle-plugin"))
  //noinspection UseTomlInstead
  implementation("org.danbrough.xtras:plugin:0.0.1-beta08")
  //noinspection UseTomlInstead
  compileOnly("com.android.tools.build:gradle:8.5.1")
}

