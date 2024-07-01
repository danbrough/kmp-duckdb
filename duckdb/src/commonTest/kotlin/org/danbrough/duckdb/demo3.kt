package org.danbrough.duckdb


fun demo3() {

  duckdb("stuff.db") {
    connect {
      query("CREATE TABLE IF NOT EXISTS stuff(id INTEGER PRIMARY KEY,name VARCHAR)") {}

      val size = query("SELECT COUNT(*) FROM stuff") {
        get<ULong>(0, 0)
      }
      log.info { "stuff size: $size" }

      if (size == 0UL) {
        log.info { "inserting ${stuffItems.size} items.." }
        append("stuff") {
          stuffItems.forEachIndexed { id, name ->
            row {
              append(id).append(name)
            }
          }
        }
      }

      query("SELECT name FROM stuff LIMIT 1000") {
        repeat(rowCount.toInt()) {
          println(get<String>(it.toLong(), 0))
        }
      }
    }
  }
}
