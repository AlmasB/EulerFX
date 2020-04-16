package eulerfx.core.euler

import eulerfx.core.algorithms.visualCenter
import javafx.geometry.Point2D
import javafx.scene.shape.Shape
import math.geom2d.polygon.MultiPolygon2D
import math.geom2d.polygon.Polygon2D
import math.geom2d.polygon.Polygons2D
import java.util.*

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * A closed curve, c (element of C).
 */
abstract class Curve(val label: Label) : Comparable<Curve> {

    /**
     * @return a curve model for computational geometry
     */
    val polygon by lazy { computePolygon() }

    /**
     * @return a bitmap view for rendering
     */
    val shape by lazy { computeShape() }

    protected abstract fun computePolygon(): Polygon2D

    protected abstract fun computeShape(): Shape

    abstract fun translate(translate: Point2D): Curve

    abstract fun scale(scale: Double, pivot: Point2D): Curve

    abstract fun toDebugString(): String

    override fun compareTo(other: Curve): Int {
        return label.compareTo(other.label)
    }

    override fun toString() = label
}

/**
 * A zone, z (element of Z), in an Euler diagram.
 */
class Zone(

        /**
         * Abstract representation of this zone.
         */
        val az: AbstractZone,

        /**
         * All curves present in the Euler diagram (the diagram to which this zone belongs).
         */
        diagramCurves: Set<Curve>) {

    /**
     * Curves inside this zone.
     */
    val containingCurves = diagramCurves.filter { it.label in az }.toSortedSet()

    /**
     * Curves outside of this zone.
     */
    val excludingCurves = (diagramCurves - containingCurves).toSortedSet()

    init {
        require(containingCurves.size == az.numLabels) { "Abstract: $az does not match concrete: $containingCurves" }
    }

    val shape by lazy { computeShape() }
    val polygon by lazy { computePolygon() }
    val visualCenter by lazy { computeVisualCenter() }

    private fun computeShape(): Shape {
        val initialShape = containingCurves.map { it.shape }
                .reduce { s1, s2 -> Shape.intersect(s1, s2) }

        return excludingCurves.fold(initialShape) { s, curve -> Shape.subtract(s, curve.shape) }
    }

    private fun computeVisualCenter(): Point2D {
        check (az != AbstractZone.OUTSIDE) { "Outside zone does not have a visual center" }

        return polygon.visualCenter()
    }

    private fun computePolygon(): Polygon2D {
        val initialPolygon = containingCurves.map { it.polygon }
                .reduce { p1, p2 -> Polygons2D.intersection(p1, p2) }

        return excludingCurves.fold(initialPolygon) { p, curve -> Polygons2D.difference(p, curve.polygon) }
    }

    fun isTopologicallyAdjacent(other: Zone): Boolean {
        if (!az.isNeighbour(other.az))
            return false

        for (v1 in polygon.vertices()) {
            for (v2 in other.polygon.vertices()) {
                if (v1.asInt == v2.asInt)
                    return true
            }
        }

        return false
    }

    fun getSeparatingCurve(other: Zone): Curve? {
        val diff = containingCurves.symmetricDifference(other.containingCurves)
        return if (diff.size == 1) diff.first() else null
    }

    fun shortestDistanceToOtherZone(point: Point2D): Double {
        if (polygon is MultiPolygon2D) {
            return Math.abs(polygon.complement().boundary().signedDistance(point.x, point.y))
        } else {
            return Math.abs(-polygon.boundary().signedDistance(point.x, point.y))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Zone)
            return false

        return az == other.az &&
                containingCurves == other.containingCurves &&
                excludingCurves == other.excludingCurves
    }

    private val hash1 = containingCurves.sumBy { it.hashCode() }
    private val hash2 = excludingCurves.sumBy { it.hashCode() }

    override fun hashCode(): Int {
        return Objects.hash(az, hash1, hash2)
    }

    override fun toString() = az.toString()
}

/**
 * An Euler diagram, d = (C, l).
 */
class EulerDiagram(val originalDescription: Description,
                   val actualDescription: Description,
                   curvesInternal: Set<Curve>) {

    val props = hashMapOf<Any, Any>()

    val curves: SortedSet<Curve> = Collections.unmodifiableSortedSet(curvesInternal.toSortedSet())

    /**
     * All zones of this Euler diagram, including shaded zones.
     * Does not include the outside zone.
     */
    val zones = actualDescription.abstractZones.minus(AbstractZone.OUTSIDE).map { Zone(it, curves) }.toSet()

    val shadedZones = zones.filter { it.az !in Z(originalDescription) }

    val outsideZone = Zone(AbstractZone.OUTSIDE, curves)

    fun getZone(az: AbstractZone): Zone {
        if (az == azEmpty)
            return outsideZone

        return zones.find { it.az == az } ?: throw IllegalArgumentException("No zone exists with abstraction $az")
    }









// TODO:
//    fun drawIntoZone(az: AbstractZone, diagram: EulerDiagram, zoneScore: Int = 1): EulerDiagram {
//
//        // [diagram] will be embedded "to" this center point
//        val newCenter: Point2D
//
//        val scaleRatio: Double
//        val new_d: EulerDiagram
//
//        if (az == azEmpty) {
//            val bbox1 = this.bbox()
//
//            val bbox2 = diagram.bbox()
//            val diagramCenter = bbox2.center()
//
//            newCenter = diagramCenter.add(bbox1.maxX + 4000.0, 0.0)
//
//            scaleRatio = 1.0
//
//            val scaledCurves = diagram.curves.map { it.scale(scaleRatio, diagramCenter) }
//
//            val newCurves = curves.plus(scaledCurves.map { it.translate(newCenter.subtract(diagramCenter)) })
//
//            new_d = EulerDiagram(originalDescription + diagram.originalDescription, actualDescription + diagram.actualDescription, newCurves)
//
//        } else {
//            val zone = zones.find { it.az == az } ?: throw Bug("No zone $az found in $this")
//
//            newCenter = zone.visualCenter
//
//            val minRadius: Double = zone.shortestDistanceToOtherZone(newCenter)
//
//            val bbox = diagram.bbox()
//            val diagramCenter = bbox.center()
//
//            val actualRadius = maxOf(bbox.width, bbox.height)
//
//            // we are embedding into az, so score of az can never be 0
//            scaleRatio = minRadius / actualRadius / maxOf(sqrt(zoneScore.toDouble()) * 0.5, 0.75)
//
//            val scaledCurves = diagram.curves.map { it.scale(scaleRatio, diagramCenter) }
//
//            val newCurves = curves.plus(scaledCurves.map { it.translate(newCenter.subtract(diagramCenter)) })
//
//            new_d = EulerDiagram(originalDescription + diagram.originalDescription, actualDescription + diagram.actualDescription, newCurves)
//        }
//
//        if (az != azEmpty) {
//            new_d.curves.filter { it.label in diagram.curves.map { it.label } }.forEach { new_d.updateLabelPosition(it) }
//        }
//
//        new_d.props["embedCenter"] = newCenter
//        new_d.props["scaleRatio"] = scaleRatio
//        return new_d
//    }
//
//    fun bbox(): Rectangle2D {
//        val polygons = curves.map { it.getPolygon() }
//        val vertices = polygons.flatMap { it.vertices() }
//
//        val minX = vertices.minBy { it.x() }!!.x()
//        val minY = vertices.minBy { it.y() }!!.y()
//        val maxX = vertices.maxBy { it.x() }!!.x()
//        val maxY = vertices.maxBy { it.y() }!!.y()
//
//        return Rectangle2D(minX, minY, maxX - minX, maxY - minY)
//    }
//
//    fun size(): Double {
//        val bbox = bbox()
//        return maxOf(bbox.width, bbox.height)
//    }
//
//    fun center(): Point2D {
//        val bbox = bbox()
//        return Point2D(bbox.minX + bbox.maxX / 2, bbox.minY + bbox.maxY / 2)
//    }







    fun translate(vector: Point2D): EulerDiagram {
        return EulerDiagram(originalDescription, actualDescription, curves.map { it.translate(vector) }.toSet())
    }

    fun scale(ratio: Double, pivot: Point2D): EulerDiagram {
        return EulerDiagram(originalDescription, actualDescription, curves.map { it.scale(ratio, pivot) }.toSet())
    }

    override fun hashCode(): Int {
        return Objects.hash(originalDescription, actualDescription, curves, zones, shadedZones, outsideZone)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EulerDiagram)
            return false

        return originalDescription == other.originalDescription &&
                actualDescription == other.actualDescription &&
                curves == other.curves &&
                zones == other.zones &&
                shadedZones == other.shadedZones &&
                outsideZone == other.outsideZone
    }

    override fun toString(): String {
        return "ED[D=$actualDescription]"
    }
}