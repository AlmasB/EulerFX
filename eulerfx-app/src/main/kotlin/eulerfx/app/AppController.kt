package eulerfx.app

import eulerfx.core.creator.EulerDiagramCreator
import eulerfx.core.euler.Description
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.TextField

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AppController {

    @FXML
    private lateinit var renderer: Renderer

    @FXML
    private lateinit var fieldInput: TextField

    fun visualize() {
        val description = Description.from(fieldInput.text)

        val diagram = EulerDiagramCreator().drawEulerDiagram(description)

        renderer.renderEulerDiagram(diagram)
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