package eulerfx.core.euler.dual

import eulerfx.core.euler.Zone
import javafx.geometry.Point2D
import javafx.scene.shape.Shape
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class MEDVertex(val zone: Zone, val point: Point2D) {

    fun distance(other: MEDVertex) = point.distance(other.point)

    override fun hashCode() = Objects.hash(zone, point)

    override fun equals(other: Any?): Boolean {
        if (other !is MEDVertex)
            return false

        return zone == other.zone && point == other.point
    }

    override fun toString() = zone.toString()
}

class MEDEdge(val v1: MEDVertex, val v2: MEDVertex, val shape: Shape) {

    override fun toString(): String {
        return "($v1 -> $v2)"
    }
}

data class MEDCycle(val nodes: List<MEDVertex>, val edges: List<MEDEdge>) {

    lateinit var polygon: MutableList<Point2D>

    fun length() = nodes.size

    /**
     * Computes length - nodes that lie in the same zone
     */
    fun lengthUnique() = nodesUnique().size

    fun nodesUnique() = nodes.distinctBy { it.zone.toString() }

    override fun toString(): String {
        return nodes.joinToString()
    }
}

fun distinctSize(vertices: List<MEDVertex>): Int {
    return vertices.distinctBy { it.zone.toString() }.size
}