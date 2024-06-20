package org.danbrough.duckdb

import java.util.Calendar
import java.util.Date
import kotlin.test.Test

class JvmTests {
	@Test
	fun test1() {
		Calendar.getInstance().let {
			it.set(Calendar.MINUTE,0)
			it.set(Calendar.SECOND,0)
			it.set(Calendar.HOUR,0)
			it.set(Calendar.MILLISECOND,0)

			log.warn { "midnight: ${it.time.time}" }
		}
	}
}