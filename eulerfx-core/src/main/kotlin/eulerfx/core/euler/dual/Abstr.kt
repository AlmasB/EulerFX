package eulerfx.core.euler.dual

import eulerfx.core.euler.AbstractZone

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */


data class Vertex(val az: AbstractZone)

data class Edge(val v1: Vertex, val v2: Vertex)

class Cycle(val vertices: List<Vertex>, val edges: List<Edge>) {

    override fun toString(): String {
        return vertices.joinToString("-") { "${it.az}" }
    }
}