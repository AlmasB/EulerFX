package eulerfx.app

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EulerFXApp : Application() {
    override fun start(stage: Stage) {
        stage.scene = Scene(Pane(), 800.0, 600.0)
        stage.show()
    }
}

fun main() {
    Application.launch(EulerFXApp::class.java)
}