pluginManagement {
  repositories {

    maven("https://maven.danbrough.org")
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
    mavenLocal()
    maven("https://maven.danbrough.org")
    google()
    mavenCentral()
  }
}

rootProject.name = "duckdb-kmp"
include(":duckdb")

listOf("android").forEach {
  include(":demos:$it")
}


