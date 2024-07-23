import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class TrackingSimulatorTest {

    private lateinit var trackingSimulator: TrackingSimulator
    private lateinit var shipmentFactory: ShipmentFactory

    fun setUp() {
        shipmentFactory = ShipmentFactory()
        trackingSimulator = TrackingSimulator(shipmentFactory)
    }

    @Test
    fun `findShipment returns null for nonExistingShipmentId`() = runBlocking {
        setUp()
        val shipment = trackingSimulator.findShipment("nonExistingId")
        assertNull(shipment)
    }

    @Test
    fun `addShipment successfully adds newShipment`() = runBlocking {
        setUp()
        val newShipment = StandardShipment("newId", "created", "N/A", LocalDateTime.now().plusDays(1))
        trackingSimulator.addShipment(newShipment)
        val foundShipment = trackingSimulator.findShipment("newId")
        assertNotNull(foundShipment)
        assertEquals("newId", foundShipment!!.id)
    }

    @Test
    fun `processUpdate creates and adds newShipment for unknownShipmentId`() = runBlocking {
        setUp()
        val updateRequest = ShipmentUpdateRequest("unknownId", "created", System.currentTimeMillis(), "standard")
        trackingSimulator.processUpdate(updateRequest)
        // Allow time for coroutine to complete
        Thread.sleep(100)
        val shipment = trackingSimulator.findShipment("unknownId")
        assertNotNull(shipment)
        assertEquals("unknownId", shipment!!.id)
    }

    @Test
    fun `processUpdate updates existingShipment`() = runBlocking {
        setUp()
        val initialShipment = StandardShipment("existingId", "created", "N/A", LocalDateTime.now().plusDays(1))
        trackingSimulator.addShipment(initialShipment)
        val updateRequest = ShipmentUpdateRequest("existingId", "shipped", System.currentTimeMillis(), "12345")
        trackingSimulator.processUpdate(updateRequest)
        // Allow time for coroutine to complete
        Thread.sleep(100)
        val updatedShipment = trackingSimulator.findShipment("existingId")
        assertTrue(updatedShipment!!.status == "shipped")
    }

    @Test
    fun `processUpdate handles invalidUpdateType gracefully`() = runBlocking {
        setUp()
        val initialShipment = StandardShipment("existingId", "created", "N/A", LocalDateTime.now().plusDays(1))
        trackingSimulator.addShipment(initialShipment)
        val updateRequest = ShipmentUpdateRequest("existingId", "invalidType", System.currentTimeMillis(), "info")
        trackingSimulator.processUpdate(updateRequest)
        // Allow time for coroutine to complete
        Thread.sleep(100)
        val shipmentAfterInvalidUpdate = trackingSimulator.findShipment("existingId")
        assertTrue(shipmentAfterInvalidUpdate!!.updateHistory.isEmpty() ?: false)
    }
}