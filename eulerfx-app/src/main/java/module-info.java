/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module eulerfx.app {
    requires eulerfx.core;
    requires javafx.fxml;

    exports eulerfx.app to javafx.fxml, javafx.graphics;

    opens eulerfx.app to javafx.fxml;
}