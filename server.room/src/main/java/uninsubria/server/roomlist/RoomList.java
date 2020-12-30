package uninsubria.server.roomlist;

import uninsubria.server.room.Room;
import uninsubria.server.room.RoomState;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.serviceResults.ErrorMsgType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the available rooms that players can join.
 * RoomList is a sigleton and it's thread-safe.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais (minor)
 * @version 0.9.5
 */
public class RoomList {

    private ConcurrentHashMap<UUID, Room> rooms;
    private ConcurrentHashMap<UUID, Lobby> lobbies;
    private static RoomList instance;
    private ServerUDP serverUDP;

    private RoomList() {
        rooms = new ConcurrentHashMap<>();
        lobbies = new ConcurrentHashMap<>();
        serverUDP = new ServerUDP();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RoomList getInstance() {
        if(instance == null)
            instance = new RoomList();

        return instance;
    }

    /**
     * Create room.
     *
     * @param creator the creator
     * @param lobby   the lobby
     */
    public static void createRoom(PlayerWrapper creator, Lobby lobby) {
        UUID uuid = lobby.getRoomId();

        Room room = new Room(uuid, lobby.getRoomName(), lobby.getNumPlayers(),
                lobby.getLanguage(), lobby.getRuleset(), creator);

        getInstance().rooms.put(uuid, room);
        getInstance().lobbies.put(uuid, lobby);
    }

    /**
     * Allows a player to join the specified room. If it's not possible to join the room, the error list
     * passed as a parameter gets populated and returned to the player.
     *
     * @param roomId the room id
     * @param player the player
     * @param errors the errors
     */
    public static void joinRoom(UUID roomId, PlayerWrapper player, List<ErrorMsgType> errors) {
        Room room = getInstance().rooms.get(roomId);
        if (room == null) {
            errors.add(ErrorMsgType.ROOM_CLOSED);
            return;
        }
        Lobby lobby = getInstance().lobbies.get(roomId);
        int entered = room.joinRoom(player);
        if (entered == 1) {
            errors.add(ErrorMsgType.ROOM_COMM_ERROR);
            return;
        }
        if (entered == 2) {
            errors.add(ErrorMsgType.ROOM_FULL);
            return;
        }

        if(!getInstance().statusAreSync(room, lobby))
            getInstance().synchronizeStatus(room, lobby);
    }

    /**
     * Leave room.
     *
     * @param roomId   the room id
     * @param playerID the player id
     */
    public static void leaveRoom(UUID roomId, String playerID) {
        Room room = getInstance().rooms.get(roomId);
        Lobby lobby = getInstance().lobbies.get(roomId);
        room.leaveRoom(playerID);
        synchronized (RoomList.class) {
            if (room.getCurrentPlayers().size() == 0) {
                getInstance().rooms.remove(roomId);
                getInstance().lobbies.remove(roomId);
                return;
            }
        }
        if(!getInstance().statusAreSync(room, lobby))
            getInstance().synchronizeStatus(room, lobby);
    }


    /**
     * Leave game.
     *
     * @param roomId   the room id
     * @param playerID the player id
     */
    public static void leaveGame(UUID roomId, String playerID) {
//        Room room = getInstance().rooms.get(roomId);
//        room.leaveGame(playerID);
//        room.leaveRoom(playerID);
    }

    /**
     * Gets room.
     *
     * @param roomId the room id
     * @return the room
     */
    public static Room getRoom(UUID roomId) {
        return getInstance().rooms.get(roomId);
    }

    /**
     * Gets lobbies.
     *
     * @return the lobbies
     */
    public static ConcurrentHashMap<UUID, Lobby> getLobbies() {
        return getInstance().lobbies;
    }

    //  Controlla che gli status tra lobby e room combacino. Restituisce true se entrambe sono aperte o entrambe chiuse,
    //  false altrimenti.
    private boolean statusAreSync(Room room, Lobby lobby) {
        boolean roomStatus;
        boolean lobbyStatus;

        if(room.getRoomStatus().equals(RoomState.OPEN))
            roomStatus = true;
        else
            roomStatus = false;

        if(lobby.getStatus().equals(Lobby.LobbyStatus.OPEN))
            lobbyStatus = true;
        else
            lobbyStatus = false;

        return roomStatus == lobbyStatus;
    }

    // Setta lo stato della lobby in base a quello della room. Open se la room è open, closed altrimenti.
    private void synchronizeStatus(Room room, Lobby lobby) {
        if(room.getRoomStatus().equals(RoomState.OPEN))
            lobby.setStatus(Lobby.LobbyStatus.OPEN);
        else
            lobby.setStatus(Lobby.LobbyStatus.CLOSED);
    }
}
