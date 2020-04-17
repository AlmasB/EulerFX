/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module eulerfx.core {
    requires transitive kotlin.stdlib;
    requires transitive javafx.base;
    requires transitive javafx.graphics;

    requires java.desktop;
    requires javaGeom;
    requires combinatoricslib3;
    requires org.jgrapht.core;

    exports eulerfx.core.algorithms;
    exports eulerfx.core.creator;
    exports eulerfx.core.euler;
}