import androidx.compose.runtime.mutableStateListOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Shipment(
    val id: String,
    var status: String,
    var location: String,
    var expectedDeliveryDate: LocalDateTime,
    var notes: MutableList<String> = mutableStateListOf(),
    var updateHistory: MutableList<String> = mutableStateListOf()
) {

    // Observer pattern implementation
    private val observers = mutableListOf<(Shipment) -> Unit>()

    fun addObserver(observer: (Shipment) -> Unit) {
        observers.add(observer)
    }

    fun removeObserver(observer: (Shipment) -> Unit) {
        observers.remove(observer)
    }

    private fun notifyObservers() {
        println("Notifying observers of shipment $id")
        observers.forEach { it(this) }
    }

    // Method to add an update and notify observers
    fun addUpdate(update: ShippingUpdate) {
        status = update.newStatus
        updateHistory.add("Shipment went from ${update.previousStatus} to ${update.newStatus} on ${convertLongToDateTime(update.updateTimeStamp)}")

        when (update) {
            is ShippedUpdate -> {
                expectedDeliveryDate = convertLongToDateTime(update.expectedDeliveryDate)
            }
            is DelayedUpdate -> {
                expectedDeliveryDate = convertLongToDateTime(update.expectedDeliveryDate)
            }
            is LocationUpdate -> {
                location = update.location
            }
            is NoteAddedUpdate -> {
                notes.add(update.note)
            }
            // Add other update types as needed
        }

        notifyObservers()
    }

        fun convertLongToDateTime(timestamp: Long): LocalDateTime {
            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            return dateTime
    }
}
