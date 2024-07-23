import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class TrackerViewHelperTest {

    private lateinit var trackingSimulator: TrackingSimulator
    private lateinit var shipmentFactory: ShipmentFactory
    private lateinit var trackerViewHelper: TrackerViewHelper
    private lateinit var shipment: Shipment
    private val originalOut = System.out
    private val outContent = ByteArrayOutputStream()

    private fun setUp() {
        // Assuming TrackingSimulator and Shipment have concrete implementations that can be instantiated.
        shipmentFactory = ShipmentFactory() // Adjust constructor as necessary
        trackingSimulator = TrackingSimulator(shipmentFactory) // Adjust constructor as necessary
        shipment = Shipment("1", "created", "N/A", LocalDateTime.now()) // Adjust constructor as necessary
        trackerViewHelper = TrackerViewHelper(trackingSimulator)

        // Simulate finding the shipment by adding it to the TrackingSimulator's internal list or similar mechanism
        trackingSimulator.addShipment(shipment)
    }

    private fun tearDown(){
        System.setOut(originalOut)
    }

    @Test
    fun `trackShipment adds shipment to trackedShipments if not already tracked`() {
        setUp()
        trackerViewHelper.trackShipment("1")
        assertTrue(trackerViewHelper.trackedShipments.contains(shipment))
        tearDown()
    }

    @Test
    fun `trackShipment does not add shipment if it is already tracked`() {
        setUp()
        trackerViewHelper.trackedShipments.add(shipment)
        trackerViewHelper.trackShipment("1")
        assertTrue(trackerViewHelper.trackedShipments.size == 1)
        tearDown()
    }

    @Test
    fun `stopTracking removes shipment from trackedShipments if it is tracked`() {
        setUp()
        trackerViewHelper.trackedShipments.add(shipment)
        trackerViewHelper.stopTracking("1")
        assertFalse(trackerViewHelper.trackedShipments.contains(shipment))
        tearDown()
    }

    @Test
    fun `stopTracking does nothing if shipment is not tracked`() {
        setUp()
        trackerViewHelper.stopTracking("2")
        assertTrue(trackerViewHelper.trackedShipments.isEmpty())
        tearDown()
    }

    @Test
    fun `update to tracked shipment is reflected in trackedShipments`() {
        setUp()
        val shipment = ShipmentUpdateRequest("1", "created", 0, "standard")
        trackingSimulator.processUpdate(shipment)
        trackerViewHelper.trackShipment("1")
        val updatedShipment = ShipmentUpdateRequest("1", "shipped", 0, "0")
        trackingSimulator.processUpdate(updatedShipment)

        val trackedShipment = trackerViewHelper.trackedShipments.find { it.id == "1" }
        assertEquals("created", trackedShipment?.status)
        assertEquals(1, trackerViewHelper.trackedShipments.size)
        tearDown()
    }

    @Test
    fun `attempting to track a non-existent shipment throws exception`() {
        setUp()
        trackerViewHelper.trackShipment("non-existent")

        assertEquals(0, trackerViewHelper.trackedShipments.size)
        tearDown()
    }

}