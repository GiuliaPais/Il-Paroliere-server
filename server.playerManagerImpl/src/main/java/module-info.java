module server.playerManagerImpl {
    requires utils.managers.api;
    requires server.services.api;
    requires utils.business;
    requires server.services.playerServicesImpl;
    requires server.wrappers;
    exports uninsubria.server.playerManagerImpl;
}