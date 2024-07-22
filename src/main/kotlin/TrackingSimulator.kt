import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

object TrackingSimulator {
    private val shipments = mutableListOf<Shipment>()

    fun findShipment(id: String): Shipment? = shipments.find { it.id == id }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun processUpdate(updateRequest: ShipmentUpdateRequest) {
        var shipment = findShipment(updateRequest.id)

        if(shipment == null) {
            // Default values for a new shipment
            val location = "N/A"
            val deliveryDate = LocalDateTime.MIN
            val notes = mutableListOf<String>()
            val shippingUpdates = mutableListOf<String>()

            shippingUpdates.add(CreatedUpdate(updateRequest.timestamp).toString())

            addShipment(
                Shipment(
                    updateRequest.id,
                    "N/A",
                    location,
                    deliveryDate,
                    notes,
                    shippingUpdates
                )
            )


        }

        shipment = findShipment(updateRequest.id)

        val update = when (updateRequest.type) {
            "shipped" -> ShippedUpdate(updateRequest.timestamp, shipment!!.status, updateRequest.info!!.toLong())
            "location" -> LocationUpdate(updateRequest.timestamp, shipment!!.status, updateRequest.info!!)
            "delivered" -> DeliveredUpdate(updateRequest.timestamp, shipment!!.status)
            "delayed" -> DelayedUpdate(updateRequest.timestamp, shipment!!.status, updateRequest.info!!.toLong())
            "lost" -> LostUpdate(updateRequest.timestamp, shipment!!.status)
            "canceled" -> CanceledUpdate(updateRequest.timestamp, shipment!!.status)
            "note added" -> NoteAddedUpdate(updateRequest.timestamp, shipment!!.status, updateRequest.info!!)

            else -> null
        }
        shipment?.addUpdate(update?: return)
    }
}
