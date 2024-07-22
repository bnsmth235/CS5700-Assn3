import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList

open class TrackerViewHelper(private val trackingSimulator: TrackingSimulator) {
    val trackedShipments: SnapshotStateList<Shipment> = mutableStateListOf()

    fun trackShipment(id: String) {
        try {
            val shipment = trackingSimulator.findShipment(id)
            if (shipment != null) {
                if (!trackedShipments.contains(shipment)) {
                    trackedShipments.add(shipment)
                    // Subscribe to changes in the shipment
                    shipment.addObserver { updatedShipment ->
                        // Update the tracked shipment in the list
                        val index = trackedShipments.indexOfFirst { it.id == updatedShipment.id }
                        if (index != -1) {
                            trackedShipments.remove(trackedShipments[index])
                            trackedShipments.add(index, updatedShipment)
                        }
                    }
                }
            } else {
                throw Exception("Shipment not found.")
            }
        } catch (e: Exception) {
            println("Error tracking shipment: ${e.message}")
        }
    }

    fun stopTracking(id: String) {
        val shipmentToRemove = trackedShipments.find { it.id == id }
        shipmentToRemove?.let {
            it.removeObserver { /* no action needed */ }
            trackedShipments.remove(it)
        }
    }
}
