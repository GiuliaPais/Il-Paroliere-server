module server.services.api {
	exports uninsubria.server.services.api;
	
	requires java.base;
	requires org.junit.jupiter.api;
	requires org.mockito;
	requires transitive server.services.serviceInterface;
	requires server.services.playerServicesImpl;
	
	uses uninsubria.server.services.serviceInterface.Service;
}