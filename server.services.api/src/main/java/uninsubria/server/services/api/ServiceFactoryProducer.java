package uninsubria.server.services.api;

import uninsubria.server.services.serviceInterface.AbstractServiceFactory;

/**
 * 
 * @author Giulia Pais
 *
 */
public class ServiceFactoryProducer {
	
	/*---Fields---*/
	public enum FactoryType {
		PLAYER,
		ROOM
	}
	
	/*---Constructors---*/
	public ServiceFactoryProducer() {}
	
	/*---Methods---*/
	/**
	 * @throws Exception 
	 * 
	 */
	public AbstractServiceFactory getServiceFactory(FactoryType type) throws Exception {
		if (type == null | !(type instanceof FactoryType)) {
			throw new Exception("Wrong argument supplied to factory producer");
		}
		
		AbstractServiceFactory fact = switch (type) {
					case PLAYER -> /*new PlayerServiceFactory()*/null;
					case ROOM -> /*new RoomServiceFactory();*/ null;
		};
		return fact;
	}
}
