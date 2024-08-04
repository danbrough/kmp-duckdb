package org.danbrough.duckdb

fun demoVectors(){
  log.info { "demoVectors()" }
  duckdb {
    connect{
      query("SELECT CASE WHEN i%2=0 THEN NULL ELSE i END res_col FROM range(10) t(i)"){

      }
    }
  }
}


/*
duckdb_database db;
duckdb_connection con;
duckdb_open(nullptr, &db);
duckdb_connect(db, &con);

duckdb_result res;
duckdb_query(con, "SELECT CASE WHEN i%2=0 THEN NULL ELSE i END res_col FROM range(10) t(i)", &res);

// iterate until result is exhausted
while (true) {
	duckdb_data_chunk result = duckdb_fetch_chunk(res);
	if (!result) {
		// result is exhausted
		break;
	}
	// get the number of rows from the data chunk
	idx_t row_count = duckdb_data_chunk_get_size(result);
	// get the first column
	duckdb_vector res_col = duckdb_data_chunk_get_vector(result, 0);
	// get the native array and the validity mask of the vector
	int64_t *vector_data = (int64_t *) duckdb_vector_get_data(res_col);
	uint64_t *vector_validity = duckdb_vector_get_validity(res_col);
	// iterate over the rows
	for (idx_t row = 0; row < row_count; row++) {
		if (duckdb_validity_row_is_valid(vector_validity, row)) {
			printf("%lld\n", vector_data[row]);
		} else {
			printf("NULL\n");
		}
	}
	duckdb_destroy_data_chunk(&result);
}
// clean-up
duckdb_destroy_result(&res);
duckdb_disconnect(&con);
duckdb_close(&db);
 */