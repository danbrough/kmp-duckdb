package org.danbrough.duckdb


fun demo3() {

  duckdb("stuff.db") {
    connect {
      query("CREATE TABLE IF NOT EXISTS stuff(id INTEGER PRIMARY KEY,name VARCHAR)") {}

      val size = query("SELECT COUNT(*) FROM stuff") {
        get<Int>(0, 0)
      }
      log.info { "stuff size: $size" }

      if (size == 0) {
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

fun demo4() {
  duckdb {
    connect {
      query("CREATE SEQUENCE seq_id") {}

      query("select nextval('seq_id'),COLUMNS(*),3   as A  from range(DATE '1992-01-01', DATE '1992-11-01', INTERVAL '1' MONTH)") {
        log.info { "rowCount: $rowCount colCount: $columnCount rowsChanged: $rowsChanged" }
        for (n in 0 until rowCount) {
          log.debug { "id: ${get<Long>(n, 0)}, ${get<String>(n, 1)}, ${get<Int>(n, 2)}" }
        }

        /*          for (n in 0L until rowCount) {
                    log.trace { "${get<Long>(n, 0)}, ${get<String>(n, 0)}, ${get<Int>(n, 0)}" }
                  }*/

      }

      query("SELECT 1234568912345678912::INT64"){
        log.debug { "1234568912345678912 == ${get<Long>(0,0)}" }
      }
      query("select 12345689123456789123::UINT64"){
        log.debug { "12345689123456789123 == ${get<ULong>(0,0)}" }
      }
    }
  }
}