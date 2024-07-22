import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ShipmentFactory {
    fun createShipment(request: ShipmentRequest): Shipment {
        val expectedDeliveryDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.expectedDeliveryDate), ZoneId.systemDefault())
        return when (request.type) {
            "Standard" -> StandardShipment(request.id, request.status, request.location, expectedDeliveryDate)
            "Express" -> ExpressShipment(request.id, request.status, request.location, expectedDeliveryDate)
            "Overnight" -> OvernightShipment(request.id, request.status, request.location, expectedDeliveryDate)
            "Bulk" -> BulkShipment(request.id, request.status, request.location, expectedDeliveryDate)
            else -> throw IllegalArgumentException("Invalid shipment type")
        }
    }
}
