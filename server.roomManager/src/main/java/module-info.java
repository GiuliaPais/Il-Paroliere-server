module server.roomManager {
    requires utils.managers.api;
    requires utils.connection;
    requires server.services.api;
    requires server.wrappers;
    requires utils.business;
    exports uninsubria.server.roomManager;
}