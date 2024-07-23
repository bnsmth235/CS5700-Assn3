import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackingServerIntegrationTest {

    private lateinit var client: HttpClient
    private lateinit var server: TrackingServer

    fun setUp() {
        server = TrackingServer(TrackingSimulator(ShipmentFactory()))
        // Start the server in a non-blocking way
        Thread { server.start() }.start()

        // Let the server spin up
        Thread.sleep(1000)

        // Setup HttpClient for sending requests
        client = HttpClient(CIO) {
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
    }

    @Test
    fun `Server processes shipment update correctly`() = runBlocking {
        setUp()
        val updateRequest = ShipmentUpdateRequest("1", "shipped", System.currentTimeMillis(), "12345")
        val response = client.post("http://localhost:8080/shipment/update") {
                            contentType(ContentType.Application.Json)
                            setBody(ShipmentUpdateRequest(
                                id = updateRequest.id,
                                type = updateRequest.type,
                                timestamp = updateRequest.timestamp,
                                info = updateRequest.info
                            ))
                        }

        assertEquals(HttpStatusCode.OK, response.status)
        tearDown()
    }

    fun tearDown() {
        client.close()
    }
}