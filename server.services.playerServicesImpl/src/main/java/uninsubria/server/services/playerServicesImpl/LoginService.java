package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.services.api.Service;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.utils.serviceResults.ServiceResultInterface;

public class LoginService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.LOGIN;
	


	public PlayerServiceType getServiceType() {
		return serviceType ;
	}



	@Override
	public ServiceResultInterface execute() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
