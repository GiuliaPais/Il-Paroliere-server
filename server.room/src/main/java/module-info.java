module server.room {
    requires javafx.base;
    requires utils.business;
    requires server.roomReference;
    requires server.match;
    requires utils.languages;
    requires utils.ruleset;
    requires utils.connection;
    requires server.wrappers;
    requires utils.serviceResults;
    requires utils.chronometer;

    exports uninsubria.server.room;
}