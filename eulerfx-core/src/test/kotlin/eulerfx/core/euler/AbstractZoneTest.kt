package eulerfx.core.euler

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInOrder
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AbstractZoneTest {

    private lateinit var zone1: AbstractZone
    private lateinit var zone2: AbstractZone
    private lateinit var zone3: AbstractZone

    @BeforeEach
    fun setUp() {
        zone1 = az("a")
        zone2 = az("ab")
        zone3 = az("a")
    }

    @Test
    fun `Creation`() {
        assertThrows(IllegalArgumentException::class.java, {
            az("a b")
        })
    }

    @Test
    fun `Equality`() {
        assertThat(zone1, `is`(zone3))
        assertThat(zone1.hashCode(), `is`(zone3.hashCode()))

        assertThat(zone1, `is`(not(zone2)))
        assertThat(zone3, `is`(not(zone2)))

        assertThat(zone1, `is`(AbstractZone(setOf("a"))))
        assertThat(zone2, `is`(AbstractZone(setOf("a", "b"))))
        assertThat(zone2, `is`(AbstractZone(setOf("b", "a"))))

        assertFalse(zone1.equals("a"))
    }

    @Test
    fun `Comparable`() {
        val sorted = sortedSetOf(az("a"), az("c"), az("abc"), az("b"), az("ab"))

        assertThat(sorted, contains(az("a"), az("b"), az("c"), az("ab"), az("abc")))
    }

    @Test
    fun `Number of labels`() {
        assertThat(azEmpty.numLabels, `is`(0))
        assertThat(zone1.numLabels, `is`(1))
        assertThat(zone2.numLabels, `is`(2))
        assertThat(zone3.numLabels, `is`(1))
    }

    @Test
    fun `Label in abstract zone`() {
        assertTrue(zone1.contains(("a")))
        assertTrue(zone2.contains(("a")))
        assertTrue(zone3.contains(("a")))

        assertFalse(zone1.contains(("b")))
        assertTrue(zone2.contains(("b")))
        assertFalse(zone3.contains(("b")))
    }

    @Test
    fun `az + label`() {
        assertEquals(zone1, azEmpty + "a")
        assertEquals(zone3, azEmpty + "a")

        assertEquals(zone2, zone1 + "b")
        assertEquals(zone2, zone3 + "b")

        assertThat(azEmpty + "b", `is`(not(zone2)))
    }

    @Test
    fun `az - label`() {
        assertThat(zone2 - "b", `is`(zone1))
        assertThat(zone2 - "b", `is`(zone3))

        assertThat(zone2 - "b", `is`(az("a")))
        assertThat(zone1 - "a", `is`(azEmpty))
        assertThat(zone3 - "a", `is`(azEmpty))

        assertThat(zone2 - "b" - "a", `is`(azEmpty))
        assertThat(zone2 - "a" - "b", `is`(azEmpty))

        // pre-condition: "c" !in zone2
        assertThat(zone2 - "c", `is`(zone2))
    }

    @Test
    fun `az1 + az2`() {
        val az1 = az("ab")
        val az2 = az("cd")

        assertThat(az1 + az2, `is`(az("abcd")))
    }

    @Test
    fun `az1 - az2`() {
        val az1 = az("abcd")
        val az2 = az("cd")

        assertThat(az1 - az2, `is`(az("ab")))
    }

    @Test
    fun `Straddled label`() {
        assertTrue(!zone1.getStraddledLabel(zone3).isPresent)
        assertTrue(!zone2.getStraddledLabel(AbstractZone.OUTSIDE).isPresent)

        assertEquals(("a"), zone1.getStraddledLabel(AbstractZone.OUTSIDE).get())
        assertEquals(("a"), AbstractZone.OUTSIDE.getStraddledLabel(zone1).get())

        assertEquals(("b"), zone1.getStraddledLabel(zone2).get())
        assertEquals(("b"), zone2.getStraddledLabel(zone1).get())
        assertEquals(("b"), zone3.getStraddledLabel(zone2).get())

        val az1 = az("ab")
        val az2 = az("xyz")

        assertFalse(az1.getStraddledLabel(az2).isPresent)
    }

    @Test
    fun `Informal`() {
        val az1 = az("ab")
        assertThat(az1.toInformal(), `is`("ab"))

        val az2 = az("bca")
        assertThat(az2.toInformal(), `is`("abc"))

        assertThat(azEmpty.toInformal(), `is`(""))
    }

    @Test
    fun testToString() {
        assertNotEquals(zone1.toString(), zone2.toString())
        assertEquals(zone1.toString(), zone3.toString())

        assertEquals("a", zone1.toString())
        assertEquals("ab", zone2.toString())
    }
}