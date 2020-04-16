package eulerfx.core.euler.curves

import eulerfx.core.euler.Curve
import eulerfx.core.euler.Label
import javafx.scene.shape.*
import math.geom2d.Point2D
import math.geom2d.polygon.Polygon2D
import math.geom2d.polygon.SimplePolygon2D
import java.util.*

/**
 * A curve whose shape is a 2D path.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PathCurve(label: Label,
                val path: Path) : Curve(label) {

    private val cachedToString = path.elements.toString()

    init {
        // TODO:
        //path.elements.addAll(ClosePath())
    }

    override fun computeShape(): Shape = path

    override fun computePolygon(): Polygon2D {
        val moveTo = path.elements[0] as MoveTo

        val polygonPoints = arrayListOf<Point2D>()

        val p0 = Point2D(moveTo.x, moveTo.y)
        polygonPoints.add(p0)

        // drop moveTo and close()
        path.elements.drop(1).dropLast(1).forEach {
            when (it) {
                is CubicCurveTo -> {
                    val smoothFactor = 10
                    val p1 = polygonPoints.last()
                    val p2 = Point2D(it.controlX1, it.controlY1)
                    val p3 = Point2D(it.controlX2, it.controlY2)
                    val p4 = Point2D(it.x, it.y)

                    var t = 0.01
                    while (t < 1.01) {

                        polygonPoints.add(getCubicValue(p1, p2, p3, p4, t))
                        t += 1.0 / smoothFactor
                    }

                    polygonPoints.add(p4)
                }

                is LineTo -> {
                    polygonPoints.add(Point2D(it.x, it.y))
                }

                is ClosePath -> {
                    // ignore
                }

                else -> {
                    throw IllegalArgumentException("Unknown path element: $it")
                }
            }
        }

        return SimplePolygon2D(polygonPoints)
    }

    private fun getCubicValue(p1: Point2D, p2: Point2D, p3: Point2D, p4: Point2D, t: Double): Point2D {
        val x = Math.pow(1 - t, 3.0) * p1.x + 3 * t * Math.pow(1 - t, 2.0) * p2.x + 3 * t*t * (1 - t) * p3.x + t*t*t*p4.x
        val y = Math.pow(1 - t, 3.0) * p1.y + 3 * t * Math.pow(1 - t, 2.0) * p2.y + 3 * t*t * (1 - t) * p3.y + t*t*t*p4.y
        return Point2D(x, y)
    }

    override fun translate(translate: javafx.geometry.Point2D): Curve {
        val copyPath = Path()

        path.elements.forEach {
            val element: PathElement = when (it) {

                is QuadCurveTo -> {
                    QuadCurveTo(it.controlX + translate.x, it.controlY + translate.y, it.x + translate.x, it.y + translate.y)
                }

                is CubicCurveTo -> {
                    CubicCurveTo(it.controlX1 + translate.x, it.controlY1 + translate.y, it.controlX2 + translate.x, it.controlY2 + translate.y, it.x + translate.x, it.y + translate.y)
                }

                is LineTo -> {
                    LineTo(it.x + translate.x, it.y + translate.y)
                }

                is MoveTo -> {
                    MoveTo(it.x + translate.x, it.y + translate.y)
                }

                is ClosePath -> {
                    ClosePath()
                }

                else -> {
                    throw IllegalArgumentException("Unknown path element: $it")
                }
            }

            copyPath.elements.add(element)
        }

        return PathCurve(label, copyPath)
    }

    override fun scale(scale: Double, pivot: javafx.geometry.Point2D): Curve {
        val sx = (1-scale) * pivot.x
        val sy = (1-scale) * pivot.y

        val copyPath = Path()

        path.elements.forEach {
            val element: PathElement = when (it) {

                is QuadCurveTo -> {
                    QuadCurveTo(it.controlX * scale + sx, it.controlY * scale + sy, it.x * scale + sx, it.y * scale + sy)
                }

                is CubicCurveTo -> {
                    CubicCurveTo(it.controlX1 * scale + sx, it.controlY1 * scale + sy, it.controlX2 * scale + sx, it.controlY2 * scale + sy, it.x * scale + sx, it.y * scale + sy)
                }

                is LineTo -> {
                    LineTo(it.x * scale + sx, it.y * scale + sy)
                }

                is MoveTo -> {
                    MoveTo(it.x * scale + sx, it.y * scale + sy)
                }

                is ClosePath -> {
                    ClosePath()
                }

                else -> {
                    throw IllegalArgumentException("Unknown path element: $it")
                }
            }

            copyPath.elements.add(element)
        }

        return PathCurve(label, copyPath)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PathCurve)
            return false

        // hack to use string version of path but effective
        return label == other.label && cachedToString == other.cachedToString
    }

    override fun hashCode(): Int {
        return Objects.hash(label, cachedToString)
    }

    override fun toDebugString(): String {
        return "$this($path)"
    }
}