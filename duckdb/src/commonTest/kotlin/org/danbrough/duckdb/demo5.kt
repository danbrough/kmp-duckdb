package org.danbrough.duckdb

fun demo5() {
  duckdb {
    connect {
      query("CREATE TABLE items (item VARCHAR, value DECIMAL(10, 2), count INTEGER)")

      query("INSERT INTO items VALUES ('jeans', 12.34, 1), ('hammer', 56.78, 2)")

      val insertStmt = prepareStatement("INSERT INTO items VALUES (?, ?, ?)")
      insertStmt.apply {
        bind(1, "Chainsaw")
        bind(2, 43.21)
        bind(3, 3)
        execute()
      }


      query("SELECT * FROM items") {
        for (n in 0 until rowCount) {
          log.info { "${get<String>(n, 0L)}, ${get<Double>(n, 1L)}, ${get<Int>(n, 2L)}" }
        }
      }

    }
  }
}