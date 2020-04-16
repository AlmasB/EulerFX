package eulerfx.app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EulerFXApp : Application() {
    override fun start(stage: Stage) {
        stage.scene = Scene(FXMLLoader.load(javaClass.getResource("main.fxml")))
        stage.show()
    }
}

fun main() {
    Application.launch(EulerFXApp::class.java)
}