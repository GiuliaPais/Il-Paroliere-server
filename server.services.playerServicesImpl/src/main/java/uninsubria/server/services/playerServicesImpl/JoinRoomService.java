package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.room.RoomList;
import uninsubria.server.services.api.Service;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of a service that allows the player to join a room.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class JoinRoomService implements Service {
    /*---Fields---*/
    private final UUID roomID;
    private final PlayerWrapper playerWrapper;
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Join room service.
     *
     * @param roomID        the room id
     * @param playerWrapper the player wrapper
     */
    public JoinRoomService(UUID roomID, PlayerWrapper playerWrapper) {
        this.roomID = roomID;
        this.playerWrapper = playerWrapper;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        RoomList.joinRoom(roomID, playerWrapper, errorList);
        ServiceResultInterface serviceResult = new ServiceResult("JOIN ROOM");
        if (errorList.isEmpty()) {
            Result<Boolean> result = new Result<>("Success", true);
            serviceResult.addResult(result);
            return serviceResult;
        }
        Result<Boolean> result = new Result<>("Success", false);
        ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
        Result<ErrorMsgType[]> errors = new Result<>("Errors", errorList.toArray(errArr));
        serviceResult.addResult(result);
        serviceResult.addResult(errors);
        return serviceResult;
    }
}
