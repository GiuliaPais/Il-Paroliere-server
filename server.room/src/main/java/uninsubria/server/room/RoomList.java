package uninsubria.server.room;

import uninsubria.server.roomReference.RoomState;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.serviceResults.ErrorMsgType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    public static RoomList getInstance() {
        if(instance == null)
            instance = new RoomList();

        return instance;
    }

    public static void createRoom(PlayerWrapper creator, Lobby lobby) {
        UUID uuid = lobby.getRoomId();

        Room room = new Room(uuid, lobby.getRoomName(), lobby.getNumPlayers(),
                lobby.getLanguage(), lobby.getRuleset(), creator);

        getInstance().rooms.put(uuid, room);
        getInstance().lobbies.put(uuid, lobby);
    }

    public static void joinRoom(UUID roomId, PlayerWrapper player, List<ErrorMsgType> errors) {
        Room room = getInstance().rooms.get(roomId);
        Lobby lobby = getInstance().lobbies.get(roomId);
        boolean entered = room.joinRoom(player);
        if (!entered) {
            errors.add(ErrorMsgType.ROOM_FULL);
            return;
        }
        getInstance().serverUDP.sendPlayersList(roomId);

        if(!getInstance().statusAreSync(room, lobby))
            getInstance().synchronizeStatus(room, lobby);
    }

    public static void leaveRoom(UUID roomId, String playerID) {
        Room room = getInstance().rooms.get(roomId);
        Lobby lobby = getInstance().lobbies.get(roomId);
        room.leaveRoom(playerID);
        getInstance().serverUDP.sendPlayersList(roomId);

        if(!getInstance().statusAreSync(room, lobby))
            getInstance().synchronizeStatus(room, lobby);
    }

    public static void leaveGame(UUID roomId, String playerID) {
        Room room = getInstance().rooms.get(roomId);
        room.leaveGame(playerID);
        room.leaveRoom(playerID);
    }

    public static Room getRoom(UUID roomId) {
        return getInstance().rooms.get(roomId);
    }

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
