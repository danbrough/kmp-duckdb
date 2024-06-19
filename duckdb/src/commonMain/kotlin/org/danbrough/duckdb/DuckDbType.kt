package org.danbrough.duckdb



/**
	Generated from the :generateTypesEnum gradle task
**/

enum class DuckDbType{
	INVALID,//0
	BOOLEAN,//1
	TINYINT,//2
	SMALLINT,//3
	INTEGER,//4
	BIGINT,//5
	UTINYINT,//6
	USMALLINT,//7
	UINTEGER,//8
	UBIGINT,//9
	FLOAT,//10
	DOUBLE,//11
	TIMESTAMP,//12
	DATE,//13
	TIME,//14
	INTERVAL,//15
	HUGEINT,//16
	VARCHAR,//17
	BLOB,//18
	DECIMAL,//19
	TIMESTAMP_S,//20
	TIMESTAMP_MS,//21
	TIMESTAMP_NS,//22
	ENUM,//23
	LIST,//24
	STRUCT,//25
	MAP,//26
	UUID,//27
	UNION,//28
	BIT,//29
	TIME_TZ,//30
	TIMESTAMP_TZ,//31
	UHUGEINT,//32
	ARRAY,//33
;
	companion object{
		fun valueOf(ord:UInt) = DuckDbType.entries[ord.toInt()]
	}
}
