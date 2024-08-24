pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    //maven("https://maven.danbrough.org")

    mavenCentral()
    gradlePluginPortal()
  }
}

includeBuild("../../../xtras/plugin") {
  name = "xtras_plugin"
}