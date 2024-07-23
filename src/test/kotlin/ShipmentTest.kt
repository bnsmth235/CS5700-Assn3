import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class ShipmentTest {

    @Test
    fun `addUpdate with ShippedUpdate updates status and expectedDeliveryDate`() {
        val shipment = StandardShipment("1", "Created", "Warehouse", LocalDateTime.now())
        val newExpectedDeliveryDate = LocalDateTime.now().plusDays(5)
        shipment.addUpdate(ShippedUpdate(0, shipment.status, newExpectedDeliveryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))

        assertEquals(shipment.status, "shipped")
        assertEquals(newExpectedDeliveryDate.toLocalDate(), shipment.expectedDeliveryDate.toLocalDate())
    }

    @Test
    fun `addUpdate with DelayedUpdate updates status and postpones expectedDeliveryDate`() {
        val initialDate = LocalDateTime.now()
        val shipment = StandardShipment("1", "shipped", "On the way", initialDate)
        val postponedDate = initialDate.plusDays(2)
        shipment.addUpdate(DelayedUpdate(0, shipment.status, postponedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))

        assertEquals("delayed", shipment.status)
        assertEquals(postponedDate.toLocalDate(), shipment.expectedDeliveryDate.toLocalDate())
    }

    @Test
    fun `addUpdate with LocationUpdate updates location`() {
        val shipment = StandardShipment("1", "Shipped", "Warehouse", LocalDateTime.now())
        val newLocation = "In transit"
        shipment.addUpdate(LocationUpdate(0, shipment.status, newLocation))

        assertEquals(newLocation, shipment.location)
    }

    @Test
    fun `addUpdate with NoteAddedUpdate adds note to notes list`() {
        val shipment = StandardShipment("1", "Pending", "Warehouse", LocalDateTime.now())
        val note = "Fragile"
        shipment.addUpdate(NoteAddedUpdate(0, shipment.status, note))

        assertTrue(note in shipment.notes)
    }

    @Test
    fun `ExpressShipment validateDeliveryDate adds abnormal note if delivery date exceeds 3 days`() {
        val createdDate = LocalDateTime.now().minusDays(5)
        val shipment = ExpressShipment("1", "Pending", "Warehouse", LocalDateTime.now().plusDays(4))
        shipment.updateHistory.add("Shipment created on $createdDate")
        shipment.addUpdate(ShippedUpdate(0, shipment.status, LocalDateTime.now().plusDays(4).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))

        assertTrue(shipment.notes.any { it.contains("Abnormal: Express shipment expected delivery date is more than 3 days") })
    }

    @Test
    fun `OvernightShipment validateDeliveryDate adds abnormal note if delivery date exceeds 1 day`() {
        val createdDate = LocalDateTime.now().minusDays(2)
        val shipment = OvernightShipment("1", "Pending", "Warehouse", LocalDateTime.now().plusDays(2))
        shipment.updateHistory.add("Shipment created on $createdDate")
        shipment.addUpdate(ShippedUpdate(0, shipment.status, LocalDateTime.now().plusDays(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))

        assertTrue(shipment.notes.any { it.contains("Abnormal: Overnight shipment expected delivery date is more than 1 day") })
    }

    @Test
    fun `BulkShipment validateDeliveryDate adds abnormal note if delivery date is less than 3 days`() {
        val createdDate = LocalDateTime.now().minusDays(1)
        val shipment = BulkShipment("1", "Pending", "Warehouse", LocalDateTime.now())
        shipment.updateHistory.add("Shipment created on $createdDate")
        shipment.addUpdate(ShippedUpdate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), shipment.status, LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))

        assertTrue(shipment.notes.any { it.contains("Abnormal: Bulk shipment expected delivery date is less than 3 days") })
    }
}