pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("de.fayard.refreshVersions") version "0.60.5"
}

/*includeBuild("../../../xtras/plugin") {
  name = "xtras_plugin"
}*/

/*
dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("./gradle/libs.versions.toml"))
    }
  }
}
*/

