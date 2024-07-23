import kotlinx.coroutines.runBlocking
import org.junit.Test

class ShipmentFactoryTest {

    private val shipmentFactory = ShipmentFactory()

    @Test
    fun `createShipment returns StandardShipment for standard type`() = runBlocking {
        val request = ShipmentUpdateRequest("1", "created", 0, "standard")
        val update = CreatedUpdate(0, "standard")
        val shipment = shipmentFactory.createShipment(request, update)

        assert(shipment is StandardShipment)
    }

    @Test
    fun `createShipment returns ExpressShipment for express type`() = runBlocking {
        val request = ShipmentUpdateRequest("2", "created", 0, "express")
        val update = CreatedUpdate(0, "express")
        val shipment = shipmentFactory.createShipment(request, update)

        assert(shipment is ExpressShipment)
    }

    @Test
    fun `createShipment returns OvernightShipment for overnight type`() = runBlocking {
        val request = ShipmentUpdateRequest("3", "created", 0, "overnight")
        val update = CreatedUpdate(0, "overnight")
        val shipment = shipmentFactory.createShipment(request, update)

        assert(shipment is OvernightShipment)
    }

    @Test
    fun `createShipment returns BulkShipment for bulk type`() = runBlocking {
        val request = ShipmentUpdateRequest("4", "created", 0, "bulk")
        val update = CreatedUpdate(0, "bulk")
        val shipment = shipmentFactory.createShipment(request, update)

        assert(shipment is BulkShipment)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createShipment throws IllegalArgumentException for invalid type`() = runBlocking {
        val request = ShipmentUpdateRequest("3", "created", 0, "invalid")
        val update = CreatedUpdate(0, "invalid")
        val shipment = shipmentFactory.createShipment(request, update)
    }
}