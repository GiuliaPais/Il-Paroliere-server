package uninsubria.server.roomlist;

import uninsubria.server.room.Room;
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
 * @version 0.9.7
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
        room.roomStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Room.RoomState.FULL) | newValue.equals(Room.RoomState.GAMEON)) {
                getInstance().lobbies.get(room.getId()).setStatus(Lobby.LobbyStatus.CLOSED);
            } else {
                getInstance().lobbies.get(room.getId()).setStatus(Lobby.LobbyStatus.OPEN);
            }
        });
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
        int entered = room.joinRoom(player);
        if (entered == 1) {
            errors.add(ErrorMsgType.ROOM_COMM_ERROR);
            return;
        }
        if (entered == 2) {
            errors.add(ErrorMsgType.ROOM_FULL);
            return;
        }
    }

    /**
     * Leave room.
     *
     * @param roomId   the room id
     * @param playerID the player id
     */
    public static void leaveRoom(UUID roomId, String playerID) {
        Room room = getInstance().rooms.get(roomId);
        room.leaveRoom(playerID);
        synchronized (RoomList.class) {
            if (room.getCurrentPlayers().size() == 0) {
                getInstance().rooms.remove(roomId);
                getInstance().lobbies.remove(roomId);
                return;
            }
        }
    }


    /**
     * Leave game.
     *
     * @param roomId   the room id
     * @param playerID the player id
     */
    public static void leaveGame(UUID roomId, String playerID) {
        Room room = getInstance().rooms.get(roomId);
        room.leaveGame(playerID);
        synchronized (RoomList.class) {
            if (room.getCurrentPlayers().size() == 0) {
                getInstance().rooms.remove(roomId);
                getInstance().lobbies.remove(roomId);
                return;
            }
        }
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
}
