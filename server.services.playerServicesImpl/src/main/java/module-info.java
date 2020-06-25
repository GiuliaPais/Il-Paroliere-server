module server.services.playerServicesImpl {
	requires java.base;
	requires transitive server.services.api;
	
	exports uninsubria.server.services.playerServicesImpl;
	exports uninsubria.server.services.playerServicesTypes;
	
}