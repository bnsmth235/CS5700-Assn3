interface ShippingUpdate {
    val previousStatus: String
    val newStatus: String
    val updateTimeStamp: Long
}

class CreatedUpdate(updateTimeStampText: Long, shipmentType: String) : ShippingUpdate {
    override val newStatus: String = "created"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = "N/A"

    val shipmentType: String = shipmentType
}

class ShippedUpdate(updateTimeStampText: Long, previousStatusText: String, expectedDeliveryDateText: Long) : ShippingUpdate {
    override val newStatus: String = "shipped"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText

    val expectedDeliveryDate: Long = expectedDeliveryDateText
}

class LocationUpdate(updateTimeStampText: Long, previousStatusText: String, locationText: String) : ShippingUpdate {
    override val newStatus: String = "location"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText

    val location: String = locationText
}

class DeliveredUpdate(updateTimeStampText: Long, previousStatusText: String) : ShippingUpdate {
    override val newStatus: String = "delivered"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText
}

class DelayedUpdate(updateTimeStampText: Long, previousStatusText: String, newExpectedDeliveryDateText: Long) : ShippingUpdate {
    override val newStatus: String = "delayed"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText

    val expectedDeliveryDate: Long = newExpectedDeliveryDateText
}

class LostUpdate(updateTimeStampText: Long, previousStatusText: String) : ShippingUpdate {
    override val newStatus: String = "lost"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText
}

class CanceledUpdate(updateTimeStampText: Long, previousStatusText: String) : ShippingUpdate {
    override val newStatus: String = "canceled"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText
}

class NoteAddedUpdate(updateTimeStampText: Long, previousStatusText: String, noteText: String) : ShippingUpdate {
    override val newStatus: String = "note added"
    override val updateTimeStamp: Long = updateTimeStampText
    override val previousStatus: String = previousStatusText

    val note: String = noteText
}
