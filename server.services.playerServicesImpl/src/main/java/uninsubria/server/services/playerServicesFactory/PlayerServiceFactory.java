package uninsubria.server.services.playerServicesFactory;

import java.lang.reflect.Constructor;

import uninsubria.server.services.playerServicesImpl.CreateRoomService;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.server.services.serviceInterface.AbstractServiceFactory;
import uninsubria.server.services.serviceInterface.Service;
import uninsubria.server.services.serviceInterface.ServiceType;

public class PlayerServiceFactory extends AbstractServiceFactory {
	
	/*---Fields---*/

	
	/*---Constructors---*/
	public PlayerServiceFactory() {
		super();
	}
	
	/*---Methods---*/
	
	@Override
	public Service getService(ServiceType st, Object... fields) throws Exception {
		if (!(st instanceof PlayerServiceType)) {
			throw new Exception("Wrong type of service requested");
		}
		PlayerServiceType type = (PlayerServiceType) st;
		Service res = switch (type) {
			case CREATE_ROOM -> null;
			case LOGIN -> null;
		};
		return res;
	}
	
	
}
