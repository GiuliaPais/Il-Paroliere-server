package uninsubria.server.playerManagerImpl;


import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.business.Player;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Class responsible for creating and executing player requested services.
 *
 * @author Giulia Pais
 * @version 0.9.7
 */
public class PlayerManager implements PlayerManagerInterface {
    /*---Fields---*/
    private final AbstractServiceFactory serviceFactory;
    private final InetAddress playerAddress;
    private Player player;
    private UUID activeRoomID;

    /*---Constructors---*/
    /**
     * Instantiates a new Player manager.
     *
     * @param ipAddress the player ip address
     */
    public PlayerManager(InetAddress ipAddress) {
        this.playerAddress = ipAddress;
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
        Service activCodeService = serviceFactory.getService(PlayerServiceType.ACTIVATION_CODE, userID, email, name, lastname, password);
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
    public ServiceResultInterface resendCode(String email, String requestType) {
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

    @Override
    public void quit() {
        if (this.activeRoomID != null) {
            leaveRoom(activeRoomID);
        }
        if (this.player != null) {
           logout(player.getPlayerID());
        }
    }

    @Override
    public ServiceResultInterface resetPassword(String email) {
        UUID generatedPw = UUID.randomUUID();
        Service service = serviceFactory.getService(PlayerServiceType.RESET_PW, email, generatedPw);
        return service.execute();
    }

    @Override
    public ServiceResultInterface deleteProfile(String id, String password) {
        Service service = serviceFactory.getService(PlayerServiceType.DELETE_PROFILE, id, password);
        return service.execute();
    }

    @Override
    public boolean createRoom(Lobby lobby) {
        PlayerWrapper playerWrapper = new PlayerWrapper(this.player, this.playerAddress);
        Service service = serviceFactory.getService(PlayerServiceType.CREATE_ROOM, playerWrapper, lobby);
        service.execute();
        activeRoomID = lobby.getRoomId();
        return true;
    }

    @Override
    public void leaveRoom(UUID roomID) {
        Service service = serviceFactory.getService(PlayerServiceType.LEAVE_ROOM, roomID, player.getPlayerID());
        service.execute();
        activeRoomID = null;
    }

    @Override
    public ServiceResultInterface joinRoom(UUID roomID) {
        PlayerWrapper playerWrapper = new PlayerWrapper(this.player, this.playerAddress);
        Service service = serviceFactory.getService(PlayerServiceType.JOIN_ROOM, roomID, playerWrapper);
        ServiceResultInterface serviceResult = service.execute();
        Boolean joined = (Boolean) serviceResult.getResult("Success").getValue();
        if (joined) {
            activeRoomID = roomID;
        }
        return serviceResult;
    }

    @Override
    public ServiceResultInterface fetchStatistics() {
        Service service = serviceFactory.getService(PlayerServiceType.FETCH_STATS);
        return service.execute();
    }

    @Override
    public ServiceResultInterface requestWordStats(String word) {
        Service service = serviceFactory.getService(PlayerServiceType.WORD_STATS, word);
        return service.execute();
    }
}
