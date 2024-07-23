import androidx.compose.runtime.mutableStateListOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// Base Shipment class
open class Shipment(
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

    protected fun notifyObservers() {
        println("Notifying observers of shipment $id")
        observers.forEach { it(this) }
    }

    // Method to add an update and notify observers
    open fun addUpdate(update: ShippingUpdate) {
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

    protected fun convertLongToDateTime(timestamp: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return dateTime
    }
}

// Standard Shipment class
class StandardShipment(
    id: String,
    status: String,
    location: String,
    expectedDeliveryDate: LocalDateTime,
    notes: MutableList<String> = mutableStateListOf(),
    updateHistory: MutableList<String> = mutableStateListOf()
) : Shipment(id, status, location, expectedDeliveryDate, notes, updateHistory)

// Express Shipment class
class ExpressShipment(
    id: String,
    status: String,
    location: String,
    expectedDeliveryDate: LocalDateTime,
    notes: MutableList<String> = mutableStateListOf(),
    updateHistory: MutableList<String> = mutableStateListOf()
) : Shipment(id, status, location, expectedDeliveryDate, notes, updateHistory) {

    override fun addUpdate(update: ShippingUpdate) {
        super.addUpdate(update)
        if (update is ShippedUpdate || update is DelayedUpdate) {
            validateDeliveryDate()
        }
    }

    private fun validateDeliveryDate() {
        val createdDate = LocalDateTime.parse(updateHistory.last().split(" ")[7])
        if (expectedDeliveryDate.isAfter(createdDate.plusDays(3))) {
            notes.add("Abnormal: Express shipment expected delivery date is more than 3 days")
            notifyObservers()
        }
    }
}

// Overnight Shipment class
class OvernightShipment(
    id: String,
    status: String,
    location: String,
    expectedDeliveryDate: LocalDateTime,
    notes: MutableList<String> = mutableStateListOf(),
    updateHistory: MutableList<String> = mutableStateListOf()
) : Shipment(id, status, location, expectedDeliveryDate, notes, updateHistory) {

    override fun addUpdate(update: ShippingUpdate) {
        super.addUpdate(update)
        if (update is ShippedUpdate || update is DelayedUpdate) {
            validateDeliveryDate()
        }
    }

    private fun validateDeliveryDate() {
        val createdDate = LocalDateTime.parse(updateHistory.last().split(" ")[7])
        if (expectedDeliveryDate.isAfter(createdDate.plusDays(1))) {
            notes.add("Abnormal: Overnight shipment expected delivery date is more than 1 day")
            notifyObservers()
        }
    }
}

// Bulk Shipment class
class BulkShipment(
    id: String,
    status: String,
    location: String,
    expectedDeliveryDate: LocalDateTime,
    notes: MutableList<String> = mutableStateListOf(),
    updateHistory: MutableList<String> = mutableStateListOf()
) : Shipment(id, status, location, expectedDeliveryDate, notes, updateHistory) {

    override fun addUpdate(update: ShippingUpdate) {
        super.addUpdate(update)
        if (update is ShippedUpdate || update is DelayedUpdate) {
            validateDeliveryDate()
        }
    }

    private fun validateDeliveryDate() {
        val createdDate = LocalDateTime.parse(updateHistory.last().split(" ")[7])
        if (expectedDeliveryDate.isBefore(createdDate.plusDays(3))) {
            notes.add("Abnormal: Bulk shipment expected delivery date is less than 3 days")
            notifyObservers()
        }
    }
}
