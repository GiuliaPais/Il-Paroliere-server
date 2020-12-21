module server.match {
    requires utils.business;
    requires server.scoreCounter;
    requires server.dice;
    requires utils.languages;
    requires utils.chronometer;
    requires server.wrappers;
    requires utils.ruleset;

    exports uninsubria.server.match;
}