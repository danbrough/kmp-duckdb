package org.danbrough.duckdb.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required

const val DUCKDB_PATH = "DUCKDB_PATH"

abstract class CommandLine : CliktCommand() {
	val databasePath: String by option(
		"-d","--database",
		envvar = DUCKDB_PATH,
		help = "The path to the database"
	).required()
}