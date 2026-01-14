module com.apex {
    exports com.apex;

    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;

    opens com.apex to javafx.fxml;
    opens com.apex.gui.controller to javafx.fxml;
}