module server.services.roomServicesImpl {
    requires server.services.api;
    requires utils.ruleset;
    requires utils.languages;
    requires server.wrappers;
    requires server.database;
    requires utils.business;
    requires server.match;

    exports uninsubria.server.services.roomServicesImpl;
    exports uninsubria.server.services.roomServiceType;
}