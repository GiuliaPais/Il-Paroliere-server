module server.room {
    requires utils.ruleset;
    requires utils.languages;
    requires server.wrappers;
    requires server.roomManager;
    requires server.scoreCounter;
    requires server.dice;
    requires utils.connection;
    requires utils.serviceResults;
    requires utils.business;
    requires javafx.base;

    exports uninsubria.server.roomlist;
}