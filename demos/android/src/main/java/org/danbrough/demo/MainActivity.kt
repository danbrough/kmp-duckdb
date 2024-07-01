package org.danbrough.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.danbrough.demo.ui.theme.DuckdbkmpTheme
import org.danbrough.duckdb.Connection
import org.danbrough.duckdb.Database
import org.danbrough.duckdb.connect
import org.danbrough.duckdb.duckdb
import org.danbrough.duckdb.query

private val log = klog.logger("DEMO")


class MainActivity : ComponentActivity() {
  companion object {
    init {
      log.warn { "init .. loading libraries .." }
      System.loadLibrary("duckdb")
      log.debug { "init .. loading duckdbkt.." }
      System.loadLibrary("duckdbkt")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DuckdbkmpTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  val coroutineScope = rememberCoroutineScope()
  Surface {
    Column {
      Text(
        text = "Hello $name!",
        modifier = modifier
      )

      Button(onClick = { coroutineScope.launch(Dispatchers.IO){ test1() } }) {
        Text(
          text = "Test 1",
          modifier = modifier
        )
      }

    }

  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DuckdbkmpTheme {
    Greeting("Android")
  }
}


private lateinit var conn: Connection

suspend fun test1() {
  log.info { "test1() running" }
  log.trace { "trace message" }
  log.debug { "debugging it man" }
  if (!::conn.isInitialized) {
    conn = duckdb { connect() }
  }

  conn.apply {

    query("SELECT current_timestamp::VARCHAR") {
      log.info { get<String>(0, 0) }
    }

    query("CREATE SEQUENCE IF NOT EXISTS seq_id") {}

    query("select nextval('seq_id'),COLUMNS(*),3   as A  from range(DATE '1992-01-01', DATE '1994-11-01', INTERVAL '1' MONTH)") {
      log.info { "rowCount: $rowCount colCount: $columnCount rowsChanged: $rowsChanged" }
      for (n in 0 until rowCount) {
        log.info {
          "id: ${get<Long>(n, 0)}, ${get<String>(n, 1)}, ${get<Int>(n, 2)}"
        }
      }
    }
  }
}