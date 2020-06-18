module it.uninsubria.labB717304.serverIP {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens it.uninsubria.labB717304.serverIP to javafx.fxml;
    exports it.uninsubria.labB717304.serverIP;
}