package uninsubria.server.services.playerServicesTypes;

import uninsubria.server.services.api.ServiceType;
import uninsubria.server.services.playerServicesImpl.*;

public enum PlayerServiceType implements ServiceType {
	CREATE_ROOM (CreateRoomService.class),
	LOGIN (LoginService.class);
	
	private final Class<?> value;
	
	PlayerServiceType(Class<?> sClass) {
		this.value = sClass;
	}
	
	@Override
	public Class<?> getValue() {
		return value;
	}
	
	
}
