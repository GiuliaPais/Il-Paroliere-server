module server.match {
    requires utils.business;
    requires server.scoreCounter;
    requires server.roomReference;
    requires server.dice;
    requires utils.languages;

    exports uninsubria.server.match;
}