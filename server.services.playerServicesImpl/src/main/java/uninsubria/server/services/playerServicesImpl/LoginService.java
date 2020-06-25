package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.services.api.Service;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;

public class LoginService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.LOGIN;
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}


	public PlayerServiceType getServiceType() {
		return serviceType ;
	}
	
	
}
