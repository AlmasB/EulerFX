package eulerfx.app

import eulerfx.core.algorithms.updateLabelPlacement
import eulerfx.core.euler.Curve
import eulerfx.core.euler.EulerDiagram
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Renderer : Pane() {

    private val LABEL_FONT_SIZE = 72.0

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

        rootSceneGraph.translateX = 3840 / 2.0
        rootSceneGraph.translateY = 2160 / 2.0
    }

    fun renderEulerDiagram(diagram: EulerDiagram) {
        rootSceneGraph.children.clear()
        colorIndex = 0

        val curvesToLabels = mutableMapOf<Curve, Text>()

        diagram.curves.forEach {
            renderCurve(it)

            val text = Text(it.label)
            text.font = Font.font(LABEL_FONT_SIZE)
            text.fill = it.shape.stroke

            rootSceneGraph.children.addAll(text)

            curvesToLabels[it] = text
        }

        updateLabelPlacement(curvesToLabels)
    }

    private fun renderCurve(curve: Curve) {
        val shape = curve.shape

        shape.strokeWidth = 8.0
        shape.stroke = colors[colorIndex++]
        shape.fill = null

        rootSceneGraph.children.addAll(shape)
    }
}