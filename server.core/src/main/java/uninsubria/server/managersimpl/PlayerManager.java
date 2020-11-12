package uninsubria.server.managersimpl;

import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.utils.business.Player;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResultInterface;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;

import java.io.Serializable;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class PlayerManager implements PlayerManagerInterface {
    /*---Fields---*/
    private AbstractServiceFactory serviceFactory;
    private Player player;

    /*---Constructors---*/
    public PlayerManager() {
        this.serviceFactory = new ServiceFactoryImpl();
    }

    /*---Methods---*/
    @Override
    public Player login(String user, String pw) {
        Service loginService = serviceFactory.getService(PlayerServiceType.LOGIN, user, pw);
        ServiceResultInterface servRes = loginService.execute();
        Player player = (Player) servRes.getResultList().get(0).getValue();
        return player;
    }

    @Override
    public Player registerUser() {
        return null;
    }
}
