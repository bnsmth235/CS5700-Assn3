import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class TrackingServer(private val trackingSimulator: TrackingSimulator) {

    fun start() {
        embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                json(Json { prettyPrint = true })
            }
            routing {
                post("/shipment/update") {
                    val shipmentUpdate = call.receive<ShipmentUpdateRequest>()
                    print("Server received update: $shipmentUpdate")
                    trackingSimulator.processUpdate(shipmentUpdate)
                    call.respond(HttpStatusCode.OK, "Shipment updated")
                }
            }
        }.start(wait = true)
    }
}

@Serializable
data class ShipmentUpdateRequest(
    val id: String,
    val type: String,
    val timestamp: Long,
    val info: String?
)