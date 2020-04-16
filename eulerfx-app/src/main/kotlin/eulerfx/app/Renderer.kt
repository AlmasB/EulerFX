package eulerfx.app

import eulerfx.core.euler.Curve
import eulerfx.core.euler.EulerDiagram
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Renderer : Pane() {

    private val rootShadedZones = Pane()
    private val rootSceneGraph = Pane()

    private val colors = arrayListOf<Color>()
    private var colorIndex = 0

    init {
        // these values are adapted from "How should we use colour in ED" Andrew Blake, et al
        for (i in 0..29) {
            colors.add(Color.hsb(((i + 1) * 32).toDouble(),
                    if (i == 1 || i == 2) 0.26 else 0.55,
                    if (i == 1 || i == 2) 0.88 else 0.92))
        }

        Collections.swap(colors, 1, 9)
        Collections.swap(colors, 3, 7)

        background = Background(BackgroundFill(Color.WHITE, null, null))

        children.addAll(rootShadedZones, rootSceneGraph)
    }

    fun renderEulerDiagram(diagram: EulerDiagram) {
        diagram.curves.forEach { renderCurve(it) }
    }

    private fun renderCurve(curve: Curve) {
        val shape = curve.shape

        shape.strokeWidth = 42.0
        shape.stroke = colors[colorIndex++]
        shape.fill = null

        rootSceneGraph.children.addAll(shape)
    }
}