package org.danbrough.duckdb

import org.danbrough.duckdb.cinterops.DUCKDB_TYPE
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_ARRAY
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BIGINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BIT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BLOB
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BOOLEAN
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DATE
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DECIMAL
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DOUBLE
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_ENUM
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_FLOAT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_HUGEINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INTEGER
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INTERVAL
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INVALID
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_LIST
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_MAP
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_SMALLINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_STRUCT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIME
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_MS
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_NS
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_S
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_TZ
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIME_TZ
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TINYINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UBIGINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UHUGEINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UINTEGER
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UNION
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_USMALLINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UTINYINT
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UUID
import org.danbrough.duckdb.cinterops.DUCKDB_TYPE_VARCHAR

fun duckdbTypeToString(type: UInt) = when (type) {
	DUCKDB_TYPE_INVALID -> "DUCKDB_TYPE_INVALID"
	// bool
	DUCKDB_TYPE_BOOLEAN -> "DUCKDB_TYPE_BOOLEAN"
	// int8_t
	DUCKDB_TYPE_TINYINT -> "DUCKDB_TYPE_TINYINT"

	// int16_t
	DUCKDB_TYPE_SMALLINT -> "DUCKDB_TYPE_SMALLINT"
	// int32_t
	DUCKDB_TYPE_INTEGER -> "DUCKDB_TYPE_INTEGER"

	// int64_t
	DUCKDB_TYPE_BIGINT -> "DUCKDB_TYPE_BIGINT"

	// uint8_t
	DUCKDB_TYPE_UTINYINT -> "DUCKDB_TYPE_UTINYINT"

	// uint16_t
	DUCKDB_TYPE_USMALLINT -> "DUCKDB_TYPE_USMALLINT"

	// uint32_t
	DUCKDB_TYPE_UINTEGER -> "DUCKDB_TYPE_UINTEGER"

	// uint64_t
	DUCKDB_TYPE_UBIGINT -> "DUCKDB_TYPE_UBIGINT"

	// float
	DUCKDB_TYPE_FLOAT -> "DUCKDB_TYPE_FLOAT"

	// double
	DUCKDB_TYPE_DOUBLE -> "DUCKDB_TYPE_DOUBLE"


	// duckdb_timestamp, in microseconds
	DUCKDB_TYPE_TIMESTAMP -> "DUCKDB_TYPE_TIMESTAMP"

	// duckdb_date
	DUCKDB_TYPE_DATE -> "DUCKDB_TYPE_DATE"

	// duckdb_time
	DUCKDB_TYPE_TIME -> "DUCKDB_TYPE_TIME"

	// duckdb_interval
	DUCKDB_TYPE_INTERVAL -> "DUCKDB_TYPE_INTERVAL"

	// duckdb_hugeint
	DUCKDB_TYPE_HUGEINT -> "DUCKDB_TYPE_HUGEINT"

	// duckdb_uhugeint
	DUCKDB_TYPE_UHUGEINT -> "DUCKDB_TYPE_UHUGEINT"

	// const char*
	DUCKDB_TYPE_VARCHAR -> "DUCKDB_TYPE_VARCHAR"

	// duckdb_blob
	DUCKDB_TYPE_BLOB -> "DUCKDB_TYPE_BLOB"

	// decimal
	DUCKDB_TYPE_DECIMAL -> "DUCKDB_TYPE_DECIMAL"

	// duckdb_timestamp, in seconds
	DUCKDB_TYPE_TIMESTAMP_S -> "DUCKDB_TYPE_TIMESTAMP_S"

	// duckdb_timestamp, in milliseconds
	DUCKDB_TYPE_TIMESTAMP_MS -> "DUCKDB_TYPE_TIMESTAMP_MS"

	// duckdb_timestamp, in nanoseconds
	DUCKDB_TYPE_TIMESTAMP_NS -> "DUCKDB_TYPE_TIMESTAMP_NS"

	// enum type, only useful as logical type
	DUCKDB_TYPE_ENUM -> "DUCKDB_TYPE_ENUM"

	// list type, only useful as logical type
	DUCKDB_TYPE_LIST -> "DUCKDB_TYPE_LIST"

	// struct type, only useful as logical type
	DUCKDB_TYPE_STRUCT -> "DUCKDB_TYPE_STRUCT"

	// map type, only useful as logical type
	DUCKDB_TYPE_MAP -> "DUCKDB_TYPE_MAP"

	// duckdb_array, only useful as logical type
	DUCKDB_TYPE_ARRAY -> "DUCKDB_TYPE_ARRAY"

	// duckdb_hugeint
	DUCKDB_TYPE_UUID -> "DUCKDB_TYPE_UUID"

	// union type, only useful as logical type
	DUCKDB_TYPE_UNION -> "DUCKDB_TYPE_UNION"

	// duckdb_bit
	DUCKDB_TYPE_BIT -> "DUCKDB_TYPE_BIT"

	// duckdb_time_tz
	DUCKDB_TYPE_TIME_TZ -> "DUCKDB_TYPE_TIME_TZ"

	// duckdb_timestamp
	DUCKDB_TYPE_TIMESTAMP_TZ -> "DUCKDB_TYPE_TIMESTAMP_TZ"


	else -> error("Unknown type value: $type")
}