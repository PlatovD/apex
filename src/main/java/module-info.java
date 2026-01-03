module com.apex {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;


    opens com.apex to javafx.fxml;
    exports com.apex;
    exports com.apex.controller;
    opens com.apex.controller to javafx.fxml;
}