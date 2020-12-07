module server.match {
    requires utils.business;
    requires server.scoreCounter;
    requires server.roomReference;
    requires server.dice;
    requires utils.languages;
    requires utils.chronometer;

    exports uninsubria.server.match;
}