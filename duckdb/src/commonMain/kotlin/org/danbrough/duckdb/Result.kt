package org.danbrough.duckdb

expect interface ResultHandle : AutoCloseable


expect class Result : ResultHandle {

	val rowCount: ULong

	val columnCount: ULong

	val rowsChanged: ULong
	override fun close()

	fun getVarchar(row: ULong, col: ULong): String
	fun getULong(row: ULong, col: ULong): ULong
	fun getUInt(row: ULong, col: ULong): UInt

}