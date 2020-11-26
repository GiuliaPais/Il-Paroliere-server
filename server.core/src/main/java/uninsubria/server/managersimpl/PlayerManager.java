package uninsubria.server.managersimpl;

import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.utils.business.Player;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.IOException;

/**
 * Class responsible for creating and executing player requested services.
 *
 * @author Giulia Pais
 * @version 0.9.1
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
    public ServiceResultInterface requestActivationCode(String name, String lastname, String userID, String email, String password) throws IOException {
        Service activCodeService = serviceFactory.getService(PlayerServiceType.ACTIVATION_CODE, userID, email, name, lastname, password);
        ServiceResultInterface serviceResult = activCodeService.execute();
        return serviceResult;
    }
}
