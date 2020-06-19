module it.uninsubria.server.gui {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens it.uninsubria.server.gui to javafx.fxml;
    exports it.uninsubria.server.gui;
}