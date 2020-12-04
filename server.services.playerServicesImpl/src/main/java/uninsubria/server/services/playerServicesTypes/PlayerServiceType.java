package uninsubria.server.services.playerServicesTypes;

import uninsubria.server.services.api.ServiceType;
import uninsubria.server.services.playerServicesImpl.*;

/**
 * Collection of all the possible services that can be requested by a player.
 * @author Giulia Pais
 * @version 0.9.2
 */
public enum PlayerServiceType implements ServiceType {
	CREATE_ROOM (CreateRoomService.class),
	LOGIN (LoginService.class),
	LOGOUT(LogoutService.class),
	ACTIVATION_CODE(ActivationCodeService.class),
	CONFIRM_ACTIVATION_CODE(ConfirmActivationCodeService.class),
	RESEND_CODE(ResendCodeService.class);
	
	private final Class<?> value;
	
	PlayerServiceType(Class<?> sClass) {
		this.value = sClass;
	}
	
	@Override
	public Class<?> getValue() {
		return value;
	}
}
