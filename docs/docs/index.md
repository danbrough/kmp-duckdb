# DuckDB Kotlin Multiplatform library

A kotlin mpp library for the [duckdb](https://duckdb.org/) analytical database.
Currently supporting macos, linux and eventually jvm.


## Gradle configuration

```kotlin 
repositories{
    maven("https://maven.danbrough.org")
}

kotlin {
    commonMain {
        dependencies("org.danbrough:duckdb:1.0.0-alpha01")
    }
}

```

## Example Code

see: [demo.kt](https://github.com/danbrough/kmp-duckdb/blob/main/duckdb/src/posixTest/kotlin/org/danbrough/duckdb/demo.kt) for full source.

Run the demo with `./gradlew duckdb:runDemo`

```kotlin

import org.danbrough.duckdb.duckdb 

fun demo(args: Array<String>) {
	duckdb("stuff.db") {
		connect {
			query("CREATE TABLE IF NOT EXISTS stuff(id INTEGER PRIMARY KEY,name VARCHAR)") {}

			val size = query("SELECT COUNT(*) FROM stuff") {
				getULong(0UL, 0UL)
			}
			log.info { "stuff size: $size" }

			if (size == 0UL) {
				log.info { "inserting ${stuffItems.size} items.." }
				append("stuff") {
					stuffItems.forEachIndexed { id, name ->
						row {
							appendInt32(id).appendVarchar(name)
						}
					}
				}
			}

			query("SELECT name FROM stuff LIMIT 1000") {
				repeat(rowCount.toInt()) {
					println(getVarchar(it.toULong(), 0UL))
				}
			}
		}
	}
}

val stuffItems = listOf(
	"ligand",
	"twister",
	"bough",
	"traffic",
	"atrium",
  ...

```    


