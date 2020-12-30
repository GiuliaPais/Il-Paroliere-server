package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.roomlist.RoomList;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.UUID;

/**
 * Implementation of service that allows the player to leave a room.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class LeaveRoomService implements Service {
    /*---Fields---*/
    private final UUID roomId;
    private final String playerID;

    /*---Constructors---*/
    /**
     * Instantiates a new Leave room service.
     *
     * @param roomId   the room id
     * @param playerID the player id
     */
    public LeaveRoomService(UUID roomId, String playerID) {
        this.roomId = roomId;
        this.playerID = playerID;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        RoomList.leaveRoom(roomId, playerID);
        return null;
    }
}
