module server.dbpopulator {
    requires server.database;
    requires utils.business;
    requires utils.security;
    requires utils.languages;
    exports uninsubria.server.dbpopulator to server.core;
}