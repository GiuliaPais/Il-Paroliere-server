module server.wrappers {
    requires utils.business;
    exports uninsubria.server.wrappers to server.room, server.services.playerServicesImpl, server.playerManagerImpl;
}