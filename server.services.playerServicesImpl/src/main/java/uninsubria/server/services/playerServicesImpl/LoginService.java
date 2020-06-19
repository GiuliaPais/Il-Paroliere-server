package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.server.services.serviceInterface.Service;
import uninsubria.server.services.serviceInterface.ServiceType;

public class LoginService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.LOGIN;
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceType getServiceType() {
		return serviceType ;
	}
	
	
}
