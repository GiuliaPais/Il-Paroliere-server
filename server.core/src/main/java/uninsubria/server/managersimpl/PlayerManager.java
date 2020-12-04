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
 * @version 0.9.2
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
    public ServiceResultInterface login(String user, String pw) {
        Service loginService = serviceFactory.getService(PlayerServiceType.LOGIN, user, pw);
        ServiceResultInterface serviceResult = loginService.execute();
        player = (Player) serviceResult.getResult("Profile").getValue();
        return serviceResult;
    }

    @Override
    public ServiceResultInterface requestActivationCode(String name, String lastname, String userID, String email, String password) throws IOException {
        Service activCodeService = serviceFactory.getService(PlayerServiceType.ACTIVATION_CODE, userID, email, name, lastname, password);
        return activCodeService.execute();
    }

    @Override
    public ServiceResultInterface confirmActivationCode(String email, String code) throws IOException {
        Service service = serviceFactory.getService(PlayerServiceType.CONFIRM_ACTIVATION_CODE, email, code);
        ServiceResultInterface serviceResult = service.execute();
        player = (Player) serviceResult.getResult("Profile").getValue();
        return serviceResult;
    }

    @Override
    public ServiceResultInterface resendConde(String email, String requestType) throws IOException {
        Service service = serviceFactory.getService(PlayerServiceType.RESEND_CODE, email, requestType);
        return service.execute();
    }

    @Override
    public void logout(String userid) {
        Service service = serviceFactory.getService(PlayerServiceType.LOGOUT, userid);
        service.execute();
        this.player = null;
    }

    public void disconnect() {
        if (this.player != null) {
           logout(player.getPlayerID());
        }
    }
}
