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


class TrackingServer(private val shipmentFactory: ShipmentFactory, private val trackingSimulator: TrackingSimulator) {

    fun start() {
        embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                json(Json { prettyPrint = true })
            }
            routing {
                post("/shipment") {
                    val shipmentRequest = call.receive<ShipmentRequest>()
                    val shipment = shipmentFactory.createShipment(shipmentRequest)
                    trackingSimulator.addShipment(shipment)
                    call.respond(HttpStatusCode.Created, "Shipment created")
                }
                post("/shipment/update") {
                    val shipmentUpdate = call.receive<ShipmentUpdateRequest>()
                    trackingSimulator.processUpdate(shipmentUpdate)
                    call.respond(HttpStatusCode.OK, "Shipment updated")
                }
                get("/shipment/{id}") {
                    val id = call.parameters["id"]
                    val shipment = id?.let { trackingSimulator.findShipment(it) }
                    if (shipment != null) {
                        call.respond(shipment)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Shipment not found")
                    }
                }
            }
        }.start(wait = true)
    }
}

@Serializable
data class ShipmentRequest(val id: String, val type: String, val status: String, val location: String, val expectedDeliveryDate: Long)

@Serializable
data class ShipmentUpdateRequest(val id: String, val type: String, val timestamp: Long, val info: String?)