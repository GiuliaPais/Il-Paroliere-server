module server.services.playerServicesImpl {
	requires java.base;
	requires server.services.api;
	requires utils.serviceResults;
	requires server.database;
	requires utils.business;
    requires utils.languages;
    requires utils.ruleset;

    exports uninsubria.server.services.playerServicesImpl;
	exports uninsubria.server.services.playerServicesTypes;
	
}