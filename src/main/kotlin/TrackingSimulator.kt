import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrackingSimulator(private var shipmentFactory: ShipmentFactory) {
    private val shipments = mutableListOf<Shipment>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO) // Use a single scope for the class

    fun findShipment(id: String): Shipment? = shipments.find { it.id == id }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun processUpdate(updateRequest: ShipmentUpdateRequest) {
        coroutineScope.launch {
            try {
                var shipment = findShipment(updateRequest.id)
                if (shipment == null) {
                    // Directly await the result of async operation
                    shipment = async {
                        shipmentFactory.createShipment(updateRequest, CreatedUpdate(updateRequest.timestamp, updateRequest.info!!))
                    }.await()
                    addShipment(shipment)
                    println("Shipment created: ${updateRequest.id}")
                }

                // Move the re-fetching of shipment inside the coroutine to ensure it happens after the async operation
                shipment = findShipment(updateRequest.id) ?: return@launch

                val update = when (updateRequest.type) {
                    "shipped" -> ShippedUpdate(updateRequest.timestamp, shipment.status, updateRequest.info!!.toLong())
                    "location" -> LocationUpdate(updateRequest.timestamp, shipment.status, updateRequest.info!!)
                    "delivered" -> DeliveredUpdate(updateRequest.timestamp, shipment.status)
                    "delayed" -> DelayedUpdate(updateRequest.timestamp, shipment.status, updateRequest.info!!.toLong())
                    "lost" -> LostUpdate(updateRequest.timestamp, shipment.status)
                    "canceled" -> CanceledUpdate(updateRequest.timestamp, shipment.status)
                    "note added" -> NoteAddedUpdate(updateRequest.timestamp, shipment.status, updateRequest.info!!)
                    else -> null
                }
                update?.let {
                    shipment.addUpdate(it)
                    println("Shipment updated: ${shipment.id}")
                }
            } catch (e: Exception) {
                println("Error processing update: ${e.message}")
            }
        }
    }
}