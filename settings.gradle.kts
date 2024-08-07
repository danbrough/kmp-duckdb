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
//includeBuild("../../xtras/plugin") {
//  name = "xtras_plugin"
//}

rootProject.name = "duckdb"

include(":duckdb",":libs")

listOf("android").forEach {
  include(":demo_$it")
  project(":demo_$it").projectDir = file("demos/$it")
}



