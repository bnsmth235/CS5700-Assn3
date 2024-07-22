import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class TrackingSimulatorTest {
    @Test
    fun findShipmentTests() {
        val simulator = TrackingSimulator()
        val shipment1 = Shipment("1", "status1", "location1", LocalDateTime.now(), mutableListOf(), mutableListOf())
        val shipment2 = Shipment("2", "status2", "location2", LocalDateTime.now(), mutableListOf(), mutableListOf())

        simulator.addShipment(shipment1)
        simulator.addShipment(shipment2)

        assertEquals(shipment1, simulator.findShipment("1"))
        assertEquals(shipment2, simulator.findShipment("2"))
        assertNull(simulator.findShipment("3")) // Non-existent shipment
    }

    @Test
    fun addShipmentTest() {
        val simulator = TrackingSimulator()
        val shipment = Shipment("1", "status1", "location1", LocalDateTime.now(), mutableListOf(), mutableListOf())

        simulator.addShipment(shipment)

        assertEquals(shipment, simulator.findShipment("1"))
    }

}
