package eulerfx.core.euler

import eulerfx.core.euler.dual.MEDCycle

/**
 * Defines domain specific language API.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

// AbstractZone DSL

val azEmpty = AbstractZone.OUTSIDE

fun az(labels: Set<Label>) = AbstractZone(labels)
fun az(informalForm: String) = AbstractZone.from(informalForm)

operator fun AbstractZone.contains(label: Label) = label in labels

operator fun AbstractZone.plus(label: Label) = az(this.labels + label)

operator fun AbstractZone.minus(label: Label) = az(this.labels - label)

operator fun AbstractZone.minus(labels: Collection<Label>) = az(this.labels - labels)

operator fun AbstractZone.plus(other: AbstractZone) = az(this.labels + other.labels)

operator fun AbstractZone.minus(other: AbstractZone) = az(this.labels - other.labels)

// Description DSL

val D0 = D("")

fun D(informalDescription: String) = Description.from(informalDescription)

fun D(informalDescription: String, parent: AbstractZone) = Description.from(informalDescription, parent)

fun D(abstractZones: Set<AbstractZone>, parent: AbstractZone): Description = D(D(abstractZones), parent)

fun D(description: Description, parent: AbstractZone) = Description.from(description.toInformal(), parent)

fun D(abstractZones: Set<AbstractZone>) = Description(abstractZones)

fun L(D: Description): Set<Label> = D.labels

fun Z(D: Description): Set<AbstractZone> = D.abstractZones

operator fun Description.minus(label: Label): Description = D(Z(this).map { it - label }.toSet())

/**
 * 'Slots' [D] using its parent zone into this description.
 */
operator fun Description.plus(D: Description): Description = this + (D.parent to D)

operator fun Description.plus(pair: Pair<AbstractZone, Description>): Description {
    val D1 = this
    val (az1, D2) = pair

    require(az1 in Z(D1)) { "Cannot slot $D2 into $D1. No az $az1 in $D1" }

    return D(Z(D1) + Z(D2).map { it + az1 }, D1.parent)
}

// EulerDiagram DSL

fun C(d: EulerDiagram) = d.curves

fun Z(d: EulerDiagram) = d.zones

// TODO:
//fun combine(original: Description, diagrams: List<EulerDiagram>): EulerDiagram {
//    val actual = diagrams.map { it.actualDescription.toInformal() }.joinToString(" ")
//
//    var index = 0
//
//    return EulerDiagram(original, D(actual), diagrams.flatMap {
//        val translate = Point2D((index % 3) * 7000.0, (index++ / 3) * 7000.0)
//
//        it.curves.map { it.translate(translate) }
//    }.toSet())
//}
//
//fun combineNoTranslate(original: Description, diagrams: List<EulerDiagram>): EulerDiagram {
//    val actual = diagrams.map { it.actualDescription.toInformal() }.joinToString(" ")
//
//    var index = 0
//
//    return EulerDiagram(original, D(actual), diagrams.flatMap {
//        val translate = Point2D.ZERO
//
//        it.curves.map { it.translate(translate) }
//    }.toSet())
//}

// MEDCycle DSL

fun V(cycle: MEDCycle) = cycle.nodes

fun E(cycle: MEDCycle) = cycle.edges