package uninsubria.server.services.playerServicesTypes;

import uninsubria.server.services.api.ServiceType;
import uninsubria.server.services.playerServicesImpl.*;

/**
 * Collection of all the possible services that can be requested by a player.
 * @author Giulia Pais
 * @version 0.9.1
 */
public enum PlayerServiceType implements ServiceType {
	CREATE_ROOM (CreateRoomService.class),
	LOGIN (LoginService.class),
	ACTIVATION_CODE(ActivationCodeService.class);
	
	private final Class<?> value;
	
	PlayerServiceType(Class<?> sClass) {
		this.value = sClass;
	}
	
	@Override
	public Class<?> getValue() {
		return value;
	}
}
