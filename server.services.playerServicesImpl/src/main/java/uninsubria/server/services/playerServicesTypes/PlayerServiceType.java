package uninsubria.server.services.playerServicesTypes;

import uninsubria.server.services.serviceInterface.ServiceType;
import uninsubria.server.services.playerServicesImpl.*;

public enum PlayerServiceType implements ServiceType {
	CREATE_ROOM (CreateRoomService.class),
	LOGIN (LoginService.class);
	
	private final Class<?> value;
	
	PlayerServiceType(Class<?> sClass) {
		this.value = sClass;
	}

	public Class<?> getValue() {
		return value;
	}
	
	
}
