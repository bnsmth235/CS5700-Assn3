import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class TrackingSimulator(private var shipmentFactory: ShipmentFactory){
    private val shipments = mutableListOf<Shipment>()

    fun findShipment(id: String): Shipment? = shipments.find { it.id == id }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun processUpdate(updateRequest: ShipmentUpdateRequest) {
        var shipment = findShipment(updateRequest.id)

        if(shipment == null) {
            shipmentFactory.createShipment(updateRequest, CreatedUpdate(updateRequest.timestamp, updateRequest.info!!)).also {
                addShipment(it)
            }
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
        println("Shipment updated: ${shipment?.id}")
    }
}
