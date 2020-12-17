module server.roomReference {
    requires utils.connection;
    requires utils.business;
    requires utils.languages;
    requires utils.chronometer;
    requires server.scoreCounter;
    requires utils.ruleset;
    requires server.roomManager;

    exports uninsubria.server.roomReference;
}