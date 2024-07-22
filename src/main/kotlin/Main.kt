import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*

@Composable
fun SimulatorApp(simulator: TrackingSimulator) {
    MaterialTheme {
        UserInterface(simulator).createInterface()
    }
}

@Composable
fun TrackingApp(trackingClient: TrackingClient) {
    MaterialTheme {
        trackingClient.run()
    }
}

fun main() = application {
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val shipmentFactory = ShipmentFactory()
    val simulator = TrackingSimulator(shipmentFactory)
    val trackingServer = TrackingServer(simulator)
    val trackingClient = TrackingClient()

    applicationScope.launch {
        trackingServer.start()
    }

    Window(onCloseRequest = {
        applicationScope.cancel() // Cancel the scope to clean up coroutines
        exitApplication()
    }) {
        SimulatorApp(simulator) // This is now correctly placed within a composable context
    }

    Window(onCloseRequest = {
        applicationScope.cancel() // Ensure to cancel the scope here as well if windows close independently
        exitApplication()
    }) {
        TrackingApp(trackingClient) // This is now correctly placed within a composable context
    }
}