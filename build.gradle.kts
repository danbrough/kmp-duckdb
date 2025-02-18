import org.danbrough.xtras.logWarn

plugins {
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.compose) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.duckdb) apply false
  alias(libs.plugins.xtras)
}


tasks.register("thang") {


  actions.add {

    project.providers.exec {
      println("HELLO WORLD!")
      this.commandLine("date")

    }.standardOutput.asText.also {
      logWarn("OUTPUT: ${it.get()}")
    }

  }
}