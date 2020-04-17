package eulerfx.core.util

import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object Profiler {

    private val map = LinkedHashMap<String, Long>()

    fun start(name: String) {
        Log.d("Starting $name")

        map[name] = System.nanoTime()
    }

    fun end(name: String) {
        val time = System.nanoTime() - map[name]!!

        Log.i("%s took: %.3f sec".format(name, time / 1000000000.0))
    }
}