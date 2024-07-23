import org.junit.Test
import org.junit.Assert.assertEquals

class ShippingUpdateTest {

    @Test
    fun `CreatedUpdate initializes with correct values`() {
        val createdUpdate = CreatedUpdate(1625097600000, "standard")
        assertEquals("created", createdUpdate.newStatus)
        assertEquals(1625097600000, createdUpdate.updateTimeStamp)
        assertEquals("N/A", createdUpdate.previousStatus)
        assertEquals("standard", createdUpdate.shipmentType)
    }

    @Test
    fun `ShippedUpdate initializes with correct values`() {
        val shippedUpdate = ShippedUpdate(1625097600000, "pending", 1625184000000)
        assertEquals("shipped", shippedUpdate.newStatus)
        assertEquals(1625097600000, shippedUpdate.updateTimeStamp)
        assertEquals("pending", shippedUpdate.previousStatus)
        assertEquals(1625184000000, shippedUpdate.expectedDeliveryDate)
    }

    @Test
    fun `LocationUpdate initializes with correct values`() {
        val locationUpdate = LocationUpdate(1625097600000, "in transit", "New York")
        assertEquals("location", locationUpdate.newStatus)
        assertEquals(1625097600000, locationUpdate.updateTimeStamp)
        assertEquals("in transit", locationUpdate.previousStatus)
        assertEquals("New York", locationUpdate.location)
    }

    @Test
    fun `DeliveredUpdate initializes with correct values`() {
        val deliveredUpdate = DeliveredUpdate(1625097600000, "shipped")
        assertEquals("delivered", deliveredUpdate.newStatus)
        assertEquals(1625097600000, deliveredUpdate.updateTimeStamp)
        assertEquals("shipped", deliveredUpdate.previousStatus)
    }

    @Test
    fun `DelayedUpdate initializes with correct values`() {
        val delayedUpdate = DelayedUpdate(1625097600000, "shipped", 1625270400000)
        assertEquals("delayed", delayedUpdate.newStatus)
        assertEquals(1625097600000, delayedUpdate.updateTimeStamp)
        assertEquals("shipped", delayedUpdate.previousStatus)
        assertEquals(1625270400000, delayedUpdate.expectedDeliveryDate)
    }

    @Test
    fun `LostUpdate initializes with correct values`() {
        val lostUpdate = LostUpdate(1625097600000, "in transit")
        assertEquals("lost", lostUpdate.newStatus)
        assertEquals(1625097600000, lostUpdate.updateTimeStamp)
        assertEquals("in transit", lostUpdate.previousStatus)
    }

    @Test
    fun `CanceledUpdate initializes with correct values`() {
        val canceledUpdate = CanceledUpdate(1625097600000, "pending")
        assertEquals("canceled", canceledUpdate.newStatus)
        assertEquals(1625097600000, canceledUpdate.updateTimeStamp)
        assertEquals("pending", canceledUpdate.previousStatus)
    }

    @Test
    fun `NoteAddedUpdate initializes with correct values`() {
        val noteAddedUpdate = NoteAddedUpdate(1625097600000, "pending", "Fragile")
        assertEquals("note added", noteAddedUpdate.newStatus)
        assertEquals(1625097600000, noteAddedUpdate.updateTimeStamp)
        assertEquals("pending", noteAddedUpdate.previousStatus)
        assertEquals("Fragile", noteAddedUpdate.note)
    }
}