module server.wrappers {
    requires utils.business;
    requires server.match;
    exports uninsubria.server.wrappers to server.room, server.services.playerServicesImpl, server.playerManagerImpl, server.match, server.scoreCounter, server.roomManager, server.roomlist, server.services.roomServicesImpl, server.database;
}