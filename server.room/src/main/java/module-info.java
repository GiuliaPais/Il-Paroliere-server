module server.room {
    requires javafx.base;
    requires utils.business;
    requires server.roomReference;
    requires server.match;
    requires utils.languages;
    requires utils.ruleset;
    requires utils.connection;

    exports uninsubria.server.room;
}