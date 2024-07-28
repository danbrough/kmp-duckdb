pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/groups/staging/")

    //maven("https://maven.danbrough.org")
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("de.fayard.refreshVersions") version "0.60.5"
}

dependencyResolutionManagement {
  //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  @Suppress("UnstableApiUsage")
  repositories {
    maven("https://s01.oss.sonatype.org/content/groups/staging")
    //maven("https://maven.danbrough.org")
    google()
    mavenCentral()
  }
}

includeBuild("plugin")
//includeBuild("../../xtras/plugin")

rootProject.name = "duckdb-kmp"
include(":duckdb")
include(":demo")
project(":demo").projectDir = file("demos/android")

