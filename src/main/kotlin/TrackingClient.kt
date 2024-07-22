import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class TrackingClient {
    @Composable
    fun run() {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        var input by remember { mutableStateOf("") }
        var response by remember { mutableStateOf("") }
        var triggerCoroutine by remember { mutableStateOf(false) }

        LaunchedEffect(triggerCoroutine) {
            if (triggerCoroutine) {
                val result = client.post("http://localhost:8080/shipment/update") {
                    contentType(ContentType.Application.Json)
                    setBody(ShipmentUpdateRequest(
                        id = "1",
                        type = "shipped",
                        timestamp = System.currentTimeMillis(),
                        info = "123"
                    ))
                }.bodyAsText()
                response = result
                triggerCoroutine = false // Reset the trigger
            }
        }

        Column(Modifier.padding(16.dp)) {
            TextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Enter Shipment Update") }
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { triggerCoroutine = true }) {
                Text("Send Update")
            }
            Spacer(Modifier.height(8.dp))
            Text("Response: $response")
        }
    }
}