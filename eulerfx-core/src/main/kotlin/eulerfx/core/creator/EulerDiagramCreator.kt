package eulerfx.core.creator

import eulerfx.core.algorithms.smoothPath
import eulerfx.core.decomposition.dec
import eulerfx.core.decomposition.decA
import eulerfx.core.euler.*
import eulerfx.core.euler.curves.CircleCurve
import eulerfx.core.euler.curves.PathCurve
import eulerfx.core.euler.dual.MED
import eulerfx.core.euler.dual.MEDCycle
import eulerfx.core.recomposition.PiercingData
import eulerfx.core.recomposition.RecompositionStep
import eulerfx.core.util.Profiler
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.paint.Color
import javafx.scene.shape.ClosePath
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import java.lang.Math.sqrt

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EulerDiagramCreator {

    companion object {
        @JvmField val BASE_RADIUS = 300.0

        const val RADIUS_REDUCTION = 2.0
    }

    /**
     * Abstract zones we have processed so far.
     */
    private val abstractZones = mutableSetOf<AbstractZone>()

    /**
     * The diagram we generated so far.
     */
    private var d: EulerDiagram = EulerDiagram(D0, D0, emptySet())

    fun drawEulerDiagram(D: Description): EulerDiagram {
        Profiler.start("decA()")

        val components = decA(D)

        Profiler.end("decA()")

        // D is atomic
        if (components.size == 1) {
            Profiler.start("dec()")
            val dec = dec(D).reversed()
            Profiler.end("dec()")

            return drawAtomicDiagram(D, dec)
        }

        val diagrams = components.map { EulerDiagramCreator().drawAtomicDiagram(it, dec(it).reversed()) }

        val initial = diagrams[0]

        return diagrams.drop(1).fold(initial, { d1, d2 ->
            val az = d2.originalDescription.parent
            d1.drawIntoZone(az, d2)
        })
    }

    // TODO:
    private fun EulerDiagram.drawIntoZone(az: AbstractZone, diagram: EulerDiagram, zoneScore: Int = 1): EulerDiagram {

        // [diagram] will be embedded "to" this center point
        val newCenter: Point2D

        val scaleRatio: Double
        val new_d: EulerDiagram

        if (az == azEmpty) {
            val bbox1 = this.bbox()

            val bbox2 = diagram.bbox()
            val diagramCenter = diagram.center()

            newCenter = diagramCenter.add(bbox1.maxX + 4000.0, 0.0)

            scaleRatio = 1.0

            val scaledCurves = diagram.curves.map { it.scale(scaleRatio, diagramCenter) }

            val newCurves = curves.plus(scaledCurves.map { it.translate(newCenter.subtract(diagramCenter)) })

            new_d = EulerDiagram(originalDescription + diagram.originalDescription, actualDescription + diagram.actualDescription, newCurves)

        } else {
            val zone = zones.find { it.az == az } ?: throw IllegalArgumentException("No zone $az found in $this")

            newCenter = zone.visualCenter

            val minRadius: Double = zone.shortestDistanceToOtherZone(newCenter)

            val bbox = diagram.bbox()
            val diagramCenter = diagram.center()

            val actualRadius = maxOf(bbox.width, bbox.height)

            // we are embedding into az, so score of az can never be 0
            scaleRatio = minRadius / actualRadius / maxOf(sqrt(zoneScore.toDouble()) * 0.5, 0.75)

            val scaledCurves = diagram.curves.map { it.scale(scaleRatio, diagramCenter) }

            val newCurves = curves.plus(scaledCurves.map { it.translate(newCenter.subtract(diagramCenter)) })

            new_d = EulerDiagram(originalDescription + diagram.originalDescription, actualDescription + diagram.actualDescription, newCurves)
        }

//        if (az != azEmpty) {
//            new_d.curves.filter { it.label in diagram.curves.map { it.label } }.forEach { new_d.updateLabelPosition(it) }
//        }

        return new_d
    }

    private fun EulerDiagram.bbox(): Rectangle2D {
        val polygons = curves.map { it.polygon }
        val vertices = polygons.flatMap { it.vertices() }

        val minX = vertices.minBy { it.x() }!!.x()
        val minY = vertices.minBy { it.y() }!!.y()
        val maxX = vertices.maxBy { it.x() }!!.x()
        val maxY = vertices.maxBy { it.y() }!!.y()

        return Rectangle2D(minX, minY, maxX - minX, maxY - minY)
    }

    private fun EulerDiagram.size(): Double {
        val bbox = bbox()
        return maxOf(bbox.width, bbox.height)
    }

    private fun EulerDiagram.center(): Point2D {
        val bbox = bbox()
        return Point2D(bbox.minX + bbox.maxX / 2, bbox.minY + bbox.maxY / 2)
    }

    // in case we need to generate from a Decomposition dec
    // val description = dec.steps.last().to
    // val steps = dec.steps
    private fun drawAtomicDiagram(D: Description, steps: List<RecompositionStep>): EulerDiagram {
        steps.forEach { data ->

            Profiler.start("drawCurve: ${data.newLabel}")

            val curve = drawCurve(data)
            d = EulerDiagram(D, D(abstractZones + azEmpty, D.parent), d.curves + curve)

            Profiler.end("drawCurve: ${data.newLabel}")
        }

        return d
    }

    /**
     * Side-effects:
     *
     * 1. creates MED
     * 2. chooses a cycle
     * 3. creates a curve
     * 4. updates abstract zones
     */
    private fun drawCurve(data: RecompositionStep): Curve {
        var curve: Curve? = null

        if (numCurvesSoFar() == 0) {
            curve = CircleCurve(data.newLabel, BASE_RADIUS, BASE_RADIUS, BASE_RADIUS)

        } else if (data.isMaybeSinglePiercing()) {
            curve = tryDrawSinglePiercing(data)

        } else if (data.isMaybeDoublePiercing()) {
            curve = tryDrawDoublePiercing(data)

        } else if (data.isNested()) {
            throw IllegalArgumentException("Nested curve [$data] cannot exist in an atomic diagram")
        }

        if (curve != null) {
            abstractZones.addAll(data.splitZones.map { it + data.newLabel })
        } else {

            val modifiedDual = MED(d)

            val cycle = modifiedDual.computeCycle(data.splitZones) ?: throw IllegalArgumentException("Failed to find cycle")

            curve = when (cycle.lengthUnique()) {
                2 -> drawSinglePiercing(data.newLabel, cycle.nodesUnique().map { it.zone })

                // here a 2node or 3node cycle was upgraded to 4node
                4 -> drawDoublePiercing(data.newLabel, cycle.nodesUnique().map { it.zone })

                else -> PathCurve(data.newLabel, smooth(cycle))
            }

            // we might've used more zones to get a cycle, so we make sure we capture all of the used ones
            // we also call distinct() to ensure we don't reuse the outside zone more than once
            abstractZones.addAll(cycle.nodes.map { it.zone.az + data.newLabel }.distinct())
        }

        return curve
    }

    private fun tryDrawSinglePiercing(data: RecompositionStep): Curve? {
        if (numCurvesSoFar() == 1) {
            // special case: use slightly better position and size
            return CircleCurve(data.newLabel, BASE_RADIUS * 2, BASE_RADIUS, BASE_RADIUS)
        }

        // we include outsideZone in case
        val piercingData = PiercingData(2, data.splitZones.map { d.getZone(it) }, d.zones.plus(d.outsideZone).toList())
        if (!piercingData.isPiercing())
            return null

        return CircleCurve(data.newLabel, piercingData.center!!.x, piercingData.center.y, piercingData.radius / RADIUS_REDUCTION)
    }

    private fun tryDrawDoublePiercing(data: RecompositionStep): Curve? {
        if (numCurvesSoFar() == 2) {
            // special case: use slightly better position and size
            return CircleCurve(data.newLabel, BASE_RADIUS * 1.5, BASE_RADIUS * 2, BASE_RADIUS)
        }

        // we don't include outsideZone because there are other zones that bound
        val piercingData = PiercingData(4, data.splitZones.map { d.getZone(it) }, d.zones.toList())
        if (!piercingData.isPiercing())
            return null

        return CircleCurve(data.newLabel, piercingData.center!!.x, piercingData.center.y, piercingData.radius / RADIUS_REDUCTION)
    }

    private fun drawSinglePiercing(abstractCurve: Label, regions: List<Zone>): Curve {
        val piercingData = PiercingData(2, regions, d.zones.plus(d.outsideZone).toList())

        if (!piercingData.isPiercing()) {
            throw IllegalArgumentException("not 1-piercing")
        }

        return CircleCurve(abstractCurve, piercingData.center!!.x, piercingData.center.y, piercingData.radius / RADIUS_REDUCTION)
    }

    private fun drawDoublePiercing(abstractCurve: Label, regions: List<Zone>): Curve {
        val piercingData = PiercingData(4, regions, d.zones.toList())

        if (!piercingData.isPiercing()) {
            throw IllegalArgumentException("not 2-piercing")
        }

        return CircleCurve(abstractCurve, piercingData.center!!.x, piercingData.center.y, piercingData.radius / RADIUS_REDUCTION)
    }

    private fun smooth(cycle: MEDCycle): Path {
        val pathSegments = smoothPath(cycle.polygon)

        val newPath = Path()

        val firstPt = cycle.polygon[0]

        // add moveTo
        newPath.elements.add(MoveTo(firstPt.x, firstPt.y))

        for (path in pathSegments) {
            // drop the first moveTo
            newPath.elements.addAll(path.elements.drop(1))
        }

        // TODO: we still need to check integrity

        newPath.fill = Color.TRANSPARENT
        newPath.elements.add(ClosePath())

        return newPath
    }

    private fun numCurvesSoFar() = C(d).size
}