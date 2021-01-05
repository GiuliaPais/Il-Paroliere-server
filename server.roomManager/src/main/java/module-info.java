module server.roomManager {
    requires utils.managers.api;
    requires utils.connection;
    requires server.services.api;
    requires server.wrappers;
    requires utils.business;
    requires utils.ruleset;
    requires utils.languages;
    requires server.match;
    requires server.services.roomServicesImpl;
    exports uninsubria.server.roomManager;
}