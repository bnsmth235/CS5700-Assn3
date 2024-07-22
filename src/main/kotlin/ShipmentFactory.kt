import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ShipmentFactory {
    fun createShipment(request: ShipmentUpdateRequest, update: CreatedUpdate): Shipment {
        return when (update.shipmentType) {
            "standard" -> StandardShipment(request.id, update.newStatus, "N/A", LocalDateTime.MIN)
            "express" -> ExpressShipment(request.id, update.newStatus, "N/A", LocalDateTime.MIN)
            "overnight" -> OvernightShipment(request.id, update.newStatus, "N/A", LocalDateTime.MIN)
            "bulk" -> BulkShipment(request.id, update.newStatus, "N/A", LocalDateTime.MIN)
            else -> throw IllegalArgumentException("Invalid shipment type")
        }
    }
}
