# DuckDB for Kotlin Multiplatform

## Status 

Basic android demo working now along with jvm and native.

## Examples

```kotlin
duckdb("test.db") {
  connect {
    val sql = "SELECT '2022-10-08 13:13:34 Europe/Amsterdam'::TIMESTAMPTZ::VARCHAR"
    val timestamp = query(sql) { get<String>(0, 0) }
    log.info { "$sql => $timestamp" } //2022-10-09 00:13:34+13
  }
} 
```


## Installation

Run the [download_deps.sh](./download_deps.sh) script to download the [duckdb binaries](https://github.com/duckdb/duckdb/releases/tag/v1.0.0)
into the `bin` folder

Run the demo: `./gradlew runDemo1`
