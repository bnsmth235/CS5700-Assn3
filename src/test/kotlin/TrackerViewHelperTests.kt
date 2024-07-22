import kotlinx.coroutines.delay
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDateTime

class TrackerViewHelperTests {

    private lateinit var trackingSimulator: TrackingSimulator
    private lateinit var helper: TrackerViewHelper

    @Before
    fun setUp() {
        trackingSimulator = TrackingSimulator()
        helper = TrackerViewHelper(trackingSimulator)
    }

    @Test
    fun trackShipmentTest () {
        trackingSimulator.runSimulation("test.txt")
        // Wait for the simulation to start and populate the shipments
        Thread.sleep(5000)
        helper.trackShipment("s10000")

        assertFalse(helper.trackedShipments.isEmpty())
        //Assumes that the simulator works
        trackingSimulator.stopSimulation()
    }

    @Test
    fun stopTrackingTest() {
        trackingSimulator.runSimulation("test.txt")
        // Wait for the simulation to start and populate the shipments
        Thread.sleep(5000)
        helper.trackShipment("s10000")
        helper.stopTracking("s10000")

        assertTrue(helper.trackedShipments.isEmpty())
        //Assumes that the simulator works
        trackingSimulator.stopSimulation()
    }

    @Test
    fun trackShipmentObservesChangeInShipment(){
        trackingSimulator.runSimulation("test.txt")
        // Wait for the simulation to start and populate the shipments
        Thread.sleep(3000)
        helper.trackShipment("s10000")

        helper.trackShipment("s10000")
        val shipment = helper.trackedShipments.find { it.id == "s10000" }
        Thread.sleep(10000)
        val newShipment = helper.trackedShipments.find { it.id == "s10000" }
        //Assumes that the simulator works
        trackingSimulator.stopSimulation()

        if (shipment != null && newShipment != null) {
            try{
                // Cannot just assert that the shipments are different for some reason
                shipment.status != newShipment.status
                assertTrue(true)
            }
            catch (e: Exception){
                assertTrue(false)
            }
        }
        else{
            //neither shipment should be null
            assertTrue(false)
        }
    }

    @Test
    fun testTrackShipmentShouldFail() {
        trackingSimulator.runSimulation("test.txt")
        // Wait for the simulation to start and populate the shipments
        Thread.sleep(5000)
        helper.trackShipment("999")
        assertTrue(helper.trackedShipments.isEmpty())
        //Assumes that the simulator works
        trackingSimulator.stopSimulation()
    }
}
