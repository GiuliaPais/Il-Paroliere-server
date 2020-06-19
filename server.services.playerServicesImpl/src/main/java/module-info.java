module server.services.playerServicesImpl {
	requires java.base;
	requires transitive server.services.serviceInterface;
	
	exports uninsubria.server.services.playerServicesTypes to server.services.api;
	exports uninsubria.server.services.playerServicesImpl to server.services.api;
	
	provides uninsubria.server.services.serviceInterface.AbstractServiceFactory with
				uninsubria.server.services.playerServicesFactory.PlayerServiceFactory;
}