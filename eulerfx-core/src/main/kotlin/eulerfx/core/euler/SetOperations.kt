package eulerfx.core.euler

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

fun <T> Set<T>.symmetricDifference(other: Set<T>): Set<T> {
    return (this - other) + (other - this)
}

