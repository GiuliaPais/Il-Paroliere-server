module server.core {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires utils.connection;
    requires utils.managers.api;
    requires utils.serviceResults;
    requires server.database;
    requires server.services.api;
    requires server.services.playerServicesImpl;
    requires utils.business;
    requires com.jfoenix;
    requires java.prefs;
    requires server.dictionary;
    requires server.email;

    opens uninsubria.server.gui to javafx.fxml;
    exports uninsubria.server.gui;
}