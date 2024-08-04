package org.danbrough.duckdb
fun demo4() {
  duckdb {
    connect {
      query("CREATE SEQUENCE seq_id") {}

      query("select nextval('seq_id'),COLUMNS(*),3   as A  from range(DATE '1992-01-01', DATE '1992-11-01', INTERVAL '1' MONTH)") {
        log.info { "rowCount: $rowCount colCount: $columnCount rowsChanged: $rowsChanged" }
        for (n in 0 until rowCount) {
          log.debug { "id: ${get<Long>(n, 0)}, ${get<String>(n, 1)}, ${get<Int>(n, 2)}" }
        }
      }

      query("SELECT 1234568912345678912::INT64") {
        log.debug { "1234568912345678912 == ${get<Long>(0, 0)}" }
      }
      query("select 12345689123456789123::UINT64") {
        log.debug { "12345689123456789123 == ${get<ULong>(0, 0)}" }
      }

      val sql = "SELECT '2022-10-08 13:13:34 Europe/Amsterdam'::TIMESTAMPTZ::VARCHAR"
      val timestamp = query(sql) { get<String>(0, 0) }
      log.info { "$sql => $timestamp" } //2022-10-09 00:13:34+13
    }
  }
}