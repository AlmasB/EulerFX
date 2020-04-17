package eulerfx.core.decomposition

import eulerfx.core.algorithms.combinations2
import eulerfx.core.algorithms.partition2
import eulerfx.core.algorithms.partition2Lazy
import eulerfx.core.algorithms.partition2LazyStream
import eulerfx.core.euler.*
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.SimpleGraph

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

fun isAtomic(D: Description): Boolean {
    if (D == D0)
        return true

    val Z = Z(D) - azEmpty
    if (Z.size == 1 && L(D).size > 1)
        return false

    return isConnected(Z.toList())
    //return canSplit(D) == null
}

internal fun isConnected(zones: List<AbstractZone>): Boolean {
    println("isConnected: $zones")

    val graph = SimpleGraph<AbstractZone, ZoneEdge>(ZoneEdge::class.java)

    zones.forEach { graph.addVertex(it) }

    // go through each pair of nodes
    combinations2(zones)
            .filter { it.first.isNeighbour(it.second) }
            .forEach { graph.addEdge(it.first, it.second, ZoneEdge(it.first, it.second)) }

    return ConnectivityInspector(graph).isConnected
}










/**
 * Decomposes [D] into its atomic components.
 */
fun decA(D: Description): List<Description> {
    if (L(D).size <= 1) {
        return listOf(D)
    }

    canSplit(D)?.let { (D1, az1, D2) ->

        D1.parent = D.parent
        D2.parent = D.parent + az1

        return decA(D1) + decA(D2)
    }

    return listOf(D)
}

private fun canSplit(D: Description): Tuple3<Description, AbstractZone, Description>? {
    return partition2LazyStream(L(D)).parallel()
            .map { pair ->

                val L1 = pair.first
                val L2 = pair.second

                val Z1 = Z(D).map { it - L2 }.toSet()
                val Z2 = Z(D).map { it - L1 }.toSet()

                // generate D1 and D2
                // for each az in D1 check if + D2 gives D
                // for each az in D2 check if + D1 gives D

                val D1 = D(Z1)
                val D2 = D(Z2)

                Z(D1).forEach { az1 ->

                    // D2 'slots' into az1 of D1
                    if (sum(D1, az1, D2) == D) {
                        return@map Tuple3(D1, az1, D2)
                    }
                }

                Z(D2).forEach { az1 ->

                    // D1 'slots' into az1 of D2
                    if (sum(D2, az1, D1) == D) {
                        return@map Tuple3(D2, az1, D1)
                    }
                }

                return@map nullTuple
            }
            .filter { it !== nullTuple }
            .findAny()
            .orElse(null)
}

//private fun canSplit(D: Description): Tuple3<Description, AbstractZone, Description>? {
//    for ((L1, L2) in partition2(L(D)).also { println(it.size) }) {
//
//        val Z1 = Z(D).map { it - L2 }.toSet()
//        val Z2 = Z(D).map { it - L1 }.toSet()
//
//        // generate D1 and D2
//        // for each az in D1 check if + D2 gives D
//        // for each az in D2 check if + D1 gives D
//
//        val D1 = D(Z1)
//        val D2 = D(Z2)
//
//        Z(D1).forEach { az1 ->
//
//            // D2 'slots' into az1 of D1
//            if (sum(D1, az1, D2) == D) {
//                return Tuple3(D1, az1, D2)
//            }
//        }
//
//        Z(D2).forEach { az1 ->
//
//            // D1 'slots' into az1 of D2
//            if (sum(D2, az1, D1) == D) {
//                return Tuple3(D2, az1, D1)
//            }
//        }
//    }
//
//    return null
//}

// az1 in D1
private fun sum(D1: Description,
                az1: AbstractZone,
                D2: Description): Description = D1 + (az1 to D2)

private data class Tuple3<T, U, R>(val comp1: T, val comp2: U, val comp3: R)

private val nullTuple = Tuple3(D0, azEmpty, D0)