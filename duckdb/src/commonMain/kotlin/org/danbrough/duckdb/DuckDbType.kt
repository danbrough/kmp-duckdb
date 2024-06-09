package org.danbrough.duckdb

import kotlinx.cinterop.convert

/**
	Generated from the :generateTypesEnum gradle task
**/

enum class DuckDbType(val ord:UInt) {
	INVALID(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INVALID),//0
	BOOLEAN(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BOOLEAN),//1
	TINYINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TINYINT),//2
	SMALLINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_SMALLINT),//3
	INTEGER(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INTEGER),//4
	BIGINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BIGINT),//5
	UTINYINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UTINYINT),//6
	USMALLINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_USMALLINT),//7
	UINTEGER(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UINTEGER),//8
	UBIGINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UBIGINT),//9
	FLOAT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_FLOAT),//10
	DOUBLE(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DOUBLE),//11
	TIMESTAMP(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP),//12
	DATE(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DATE),//13
	TIME(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIME),//14
	INTERVAL(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_INTERVAL),//15
	HUGEINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_HUGEINT),//16
	VARCHAR(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_VARCHAR),//17
	BLOB(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BLOB),//18
	DECIMAL(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_DECIMAL),//19
	TIMESTAMP_S(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_S),//20
	TIMESTAMP_MS(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_MS),//21
	TIMESTAMP_NS(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_NS),//22
	ENUM(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_ENUM),//23
	LIST(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_LIST),//24
	STRUCT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_STRUCT),//25
	MAP(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_MAP),//26
	UUID(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UUID),//27
	UNION(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UNION),//28
	BIT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_BIT),//29
	TIME_TZ(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIME_TZ),//30
	TIMESTAMP_TZ(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_TIMESTAMP_TZ),//31
	UHUGEINT(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_UHUGEINT),//32
	ARRAY(org.danbrough.duckdb.cinterops.DUCKDB_TYPE_ARRAY),//33
;
	companion object{
		fun valueOf(ord:UInt) = DuckDbType.entries[ord.convert()]
	}
}
