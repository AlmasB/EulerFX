package eulerfx.app

import eulerfx.core.creator.EulerDiagramCreator
import eulerfx.core.euler.Description
import eulerfx.core.euler.EulerDiagram
import eulerfx.core.util.Examples
import eulerfx.core.util.Profiler
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AppController {

    @FXML
    private lateinit var renderer: Renderer

    @FXML
    private lateinit var fieldInput: TextField

    @FXML
    private lateinit var sliderZoom: Slider

    @FXML
    private lateinit var menuExamples: Menu

    private val executor = Executors.newSingleThreadExecutor { Thread(it).also { it.isDaemon = true } }

    fun initialize() {
        renderer.scaleXProperty().bind(sliderZoom.valueProperty())
        renderer.scaleYProperty().bind(sliderZoom.valueProperty())

        Examples.list.forEach { pair ->
            val item = MenuItem(pair.first)
            item.onAction = EventHandler { visualize(pair.second) }
            menuExamples.items.addAll(item)
        }
    }

    fun visualize() {
        val description = Description.from(fieldInput.text)

        visualize(description)
    }

    fun visualize(description: Description) {
        executor.submit(object : Task<EulerDiagram>() {
            override fun call(): EulerDiagram {
                Profiler.start("drawDiagram")

                val result = EulerDiagramCreator().drawEulerDiagram(description)

                Profiler.end("drawDiagram")

                return result
            }

            override fun succeeded() {
                Profiler.start("renderDiagram")
                renderer.renderEulerDiagram(value)
                Profiler.end("renderDiagram")
            }
        })
    }

    fun showSettingsDialog() {

    }

    fun showAboutDialog() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.headerText = "EulerFX"
        alert.contentText = "An Euler-based set visualization tool." +
                "\nDeveloper: Almas Baimagambetov (github.com/AlmasB)"
        alert.show()
    }
}