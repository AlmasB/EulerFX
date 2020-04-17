package eulerfx.core.algorithms

import eulerfx.core.euler.Curve
import eulerfx.core.euler.curves.CircleCurve
import javafx.geometry.Point2D
import javafx.scene.text.Text

/**
 * Given a curve, c, an effective point for its label, \lambda, is a 2d point that is:
 * 1. not inside c.
 * 2. inside fewest curves.
 * 3. closer to c than to other curves.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

fun updateLabelPlacement(curvesToLabels: Map<Curve, Text>) {
    val curves = curvesToLabels.keys

    curves.forEach {
        updateLabelPosition(it, curvesToLabels[it]!!, curves - it)
    }
}

private fun updateLabelPosition(curve: Curve, text: Text, otherCurves: Set<Curve>) {
    val polygon = curve.polygon
    val center = polygon.centroid()

    val labelPos = polygon.vertices()
            .map { Point2D(it.x(), it.y()) }
            .map {
                val magnitude = capMagnitude(if (curve is CircleCurve) curve.radius / 3 else 150.0)

                val r = if (curve is CircleCurve) curve.radius else 0.0

                // compute vector outwards
                it.add(it.subtract(center.x(), center.y()).normalize().multiply(magnitude))
            }
            .sortedBy {
                val minDistance = minDistanceToOtherCurves(it, otherCurves)
                val numCurves = numCurvesThatContainPoint(it, otherCurves)

                // number of curves has a more significant impact
                2000 * numCurves - minDistance
            }
            .first()!!

    text.translateX = labelPos.x
    text.translateY = labelPos.y

    println(labelPos)
}

private fun capMagnitude(mag: Double): Double {
    if (mag > 300)
        return 300.0

    if (mag < 150)
        return 150.0

    return mag
}

private fun numCurvesThatContainPoint(point: Point2D, curves: Set<Curve>) = curves.count { it.polygon.contains(point.x, point.y) }

private fun minDistanceToOtherCurves(point: Point2D, curves: Set<Curve>): Double {
    return curves.map { it.polygon.boundary().signedDistance(point.x, point.y) }
            // -20 is threshold on how "close" we think it is
            // because of polygon <-> smooth representations we might lose precision
            .filter { it >= -20 }
            .min()
            ?: 0.0
}