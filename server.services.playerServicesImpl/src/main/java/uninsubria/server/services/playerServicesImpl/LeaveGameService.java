package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.roomlist.RoomList;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.UUID;

/**
 * Implementation of a service that allows a player to leave an ongoing game.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class LeaveGameService implements Service {
    /*---Fields---*/
    private final UUID roomId;
    private final String playerID;

    /*---Constructors---*/
    /**
     * Instantiates a new Leave game service.
     *
     * @param roomId   the room id
     * @param playerID the player id
     */
    public LeaveGameService(UUID roomId, String playerID) {
        this.roomId = roomId;
        this.playerID = playerID;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        RoomList.leaveGame(roomId, playerID);
        return null;
    }
}
