import java.io.File
import kotlinx.coroutines.*
import java.nio.file.Paths
import java.time.LocalDateTime

open class TrackingSimulator {
    private val shipments = mutableListOf<Shipment>()
    private var simulationJob: Job? = null

    fun findShipment(id: String): Shipment? = shipments.find { it.id == id }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun runSimulation(filename: String) {
        // Ensure only one simulation is running at a time
        if (simulationJob?.isActive == true) {
            println("Simulation is already running.")
            return
        }

        val path = Paths.get("").toAbsolutePath().toString() + "/src/main/resources/" + filename

        simulationJob = GlobalScope.launch {
            try {
                File(path).useLines { lines ->
                    lines.forEach { line ->
                        processLine(line)
                        delay(1000) // Process one line per second
                    }
                }
                println("Simulation completed.")
            } catch (e: Exception) {
                println("Error in simulation: ${e.message}")
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
    }

    private fun processLine(line: String) {
        val lineParsed = parseLine(line)
        val type = lineParsed[0]
        val id = lineParsed[1]
        val timestamp = lineParsed[2]
        val info = lineParsed.getOrNull(3) ?: ""

        var shipment = findShipment(id)

        if (shipment == null) {
            // Default values for a new shipment
            var location = "N/A"
            var deliveryDate = LocalDateTime.MIN
            val notes = mutableListOf<String>()
            val shippingUpdates = mutableListOf<String>()

            addShipment(
                Shipment(
                    id,
                    "N/A",
                    location,
                    deliveryDate,
                    notes,
                    shippingUpdates
                )
            )

            shipment = findShipment(id)
        }

        var oldStatus = shipment?.status ?: "N/A"

        val shipmentUpdate = when (type) {
            "created" -> CreatedUpdate(timestamp.toLong())
            "shipped" -> ShippedUpdate(timestamp.toLong(), oldStatus, info.toLong())
            "location" -> LocationUpdate(timestamp.toLong(), oldStatus, info)
            "delivered" -> DeliveredUpdate(timestamp.toLong(), oldStatus)
            "delayed" -> DelayedUpdate(timestamp.toLong(), oldStatus, info.toLong())
            "lost" -> LostUpdate(timestamp.toLong(), oldStatus)
            "canceled" -> CanceledUpdate(timestamp.toLong(), oldStatus)
            "noteadded" -> NoteAddedUpdate(timestamp.toLong(), oldStatus, info)
            else -> throw IllegalArgumentException("Invalid shipment update type: $type")
        }

        shipment?.addUpdate(shipmentUpdate)
    }

    private fun parseLine(line: String): List<String> {
        return line.split(",")
    }
}
