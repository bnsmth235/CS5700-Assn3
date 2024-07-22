import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*

@Composable
fun App(simulator: TrackingSimulator) {
    MaterialTheme {
        UserInterface(simulator).createInterface()
    }
}

fun main() = application {
    val simulator = TrackingSimulator()

    // Launch the simulation in a coroutine scope
    GlobalScope.launch {
        simulator.runSimulation("test.txt")
    }

    Window(onCloseRequest = {
        simulator.stopSimulation()
        exitApplication()
    }) {
        App(simulator)
    }
}