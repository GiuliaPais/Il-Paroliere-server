module server.playerManagerImpl {
    requires utils.managers.api;
    requires server.services.api;
    requires utils.business;
    requires server.services.playerServicesImpl;
    requires server.wrappers;
    requires utils.languages;
    exports uninsubria.server.playerManagerImpl;
}