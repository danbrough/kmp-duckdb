package org.danbrough.duckdb

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

const val TASK_GENERATE_TYPES_ENUM = "generateTypesEnum"

fun Project.generateTypesEnumTask():TaskProvider<*> = tasks.register(TASK_GENERATE_TYPES_ENUM) {
  val inputFile = rootDir.resolve("headers/duckdb.h")
  val outputFile =
    project.file("src/commonMain/kotlin/org/danbrough/duckdb/DuckDbType.kt")
  inputs.file(inputFile)
  outputs.file(outputFile)

  actions.add {

    val types = inputFile.readLines().filter { it.contains("DUCKDB_TYPE_.*=".toRegex()) }.map {
      var line = it
      line = line.substringAfter("DUCKDB_TYPE_").substringBefore(',')
      val parts = line.split(" = ")
      Pair(parts[1].toInt(), parts[0])
    }.sortedWith { a, b -> a.first - b.first }

    outputFile.printWriter().use { output ->

      output.println(
        """
					package org.danbrough.duckdb
					
					/**
						Generated from the :generateTypesEnum gradle task
					**/
					
					enum class DuckDbType{
					""".trimIndent()
      )

      types.forEachIndexed { index, pair ->
        if (index != pair.first) error("index:$index != pair.first:${pair.first}")

        output.println("\t${pair.second},//${pair.first}")
      }

      output.println(
        """;
}
				""".trimIndent()
      )
    }
  }
}
