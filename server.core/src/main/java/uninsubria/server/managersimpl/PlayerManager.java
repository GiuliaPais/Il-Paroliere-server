package uninsubria.server.managersimpl;

import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.utils.business.Player;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.serviceResults.ServiceResultInterface;

/**
 * Class responsible for creating and executing player requested services.
 *
 * @author Giulia Pais
 * @version 0.9.3
 */
public class PlayerManager implements PlayerManagerInterface {
    /*---Fields---*/
    private final AbstractServiceFactory serviceFactory;
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
    public ServiceResultInterface requestActivationCode(String name, String lastname, String userID, String email, String password) {
        @SuppressWarnings("SpellCheckingInspection") Service activCodeService = serviceFactory.getService(PlayerServiceType.ACTIVATION_CODE, userID, email, name, lastname, password);
        return activCodeService.execute();
    }

    @Override
    public ServiceResultInterface confirmActivationCode(String email, String code) {
        Service service = serviceFactory.getService(PlayerServiceType.CONFIRM_ACTIVATION_CODE, email, code);
        ServiceResultInterface serviceResult = service.execute();
        player = (Player) serviceResult.getResult("Profile").getValue();
        return serviceResult;
    }

    @Override
    public ServiceResultInterface resendConde(String email, String requestType) {
        Service service = serviceFactory.getService(PlayerServiceType.RESEND_CODE, email, requestType);
        return service.execute();
    }

    @Override
    public void logout(String userid) {
        Service service = serviceFactory.getService(PlayerServiceType.LOGOUT, userid);
        service.execute();
        this.player = null;
    }

    @Override
    public void updatePlayerInfo(Player player) {
        Service service = serviceFactory.getService(PlayerServiceType.UPDATE_PLAYER_INFO, player);
        service.execute();
        this.player = player;
    }

    @Override
    public ServiceResultInterface changeUserId(String oldUserID, String newUserID) {
        Service service = serviceFactory.getService(PlayerServiceType.CHANGE_USER_ID, oldUserID, newUserID);
        ServiceResultInterface serviceResult = service.execute();
        Player updatedPlayer = (Player) serviceResult.getResult("Profile").getValue();
        if (updatedPlayer != null) {
            player = updatedPlayer;
        }
        return serviceResult;
    }

    @Override
    public ServiceResultInterface changePassword(String email, String oldPassword, String newPassword) {
        Service service = serviceFactory.getService(PlayerServiceType.CHANGE_PW, email, oldPassword, newPassword);
        ServiceResultInterface serviceResult = service.execute();
        Boolean changed = (Boolean) serviceResult.getResult("Changed").getValue();
        if (changed) {
            player.setPassword(newPassword);
        }
        return serviceResult;
    }

    public void disconnect() {
        if (this.player != null) {
           logout(player.getPlayerID());
        }
    }
}
