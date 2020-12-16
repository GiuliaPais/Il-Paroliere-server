package uninsubria.server.room;

import uninsubria.server.roomReference.RoomState;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.serviceResults.ErrorMsgType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RoomListRefactored {

    private ConcurrentHashMap<UUID, RoomRefactored> rooms;
    private ConcurrentHashMap<UUID, Lobby> lobbies;
    private static RoomListRefactored instance;
    private ServerUDP serverUDP;

    private RoomListRefactored() {
        rooms = new ConcurrentHashMap<>();
        lobbies = new ConcurrentHashMap<>();
        serverUDP = new ServerUDP();
    }

    public static RoomListRefactored getInstance() {
        if(instance == null)
            instance = new RoomListRefactored();

        return instance;
    }

    public static void createRoom(PlayerWrapper creator, Lobby lobby) {
        UUID uuid = lobby.getRoomId();

        RoomRefactored room = new RoomRefactored(uuid, lobby.getRoomName(), lobby.getNumPlayers(),
                lobby.getLanguage(), lobby.getRuleset(), creator);

        getInstance().rooms.put(uuid, room);
        getInstance().lobbies.put(uuid, lobby);
    }

    public static void joinRoom(UUID roomId, PlayerWrapper player, List<ErrorMsgType> errors) {
        RoomRefactored room = getInstance().rooms.get(roomId);
        Lobby lobby = getInstance().lobbies.get(roomId);

        if(room.getRoomStatus().equals(RoomState.OPEN)) {
            room.joinRoom(player);

            if(!getInstance().checkIfStatusAreSync(room, lobby))
                getInstance().synchronizeStatus(room, lobby);

        } else
            errors.add(ErrorMsgType.ROOM_FULL);

    }

    public static void leaveRoom(UUID roomId, String playerID) {
        RoomRefactored room = getInstance().rooms.get(roomId);
        Lobby lobby = getInstance().lobbies.get(roomId);
        room.leaveRoom(playerID);

        if(!getInstance().checkIfStatusAreSync(room, lobby))
            getInstance().synchronizeStatus(room, lobby);
    }

    public static void leaveGame(UUID roomId, String playerID) {
        RoomRefactored room = getInstance().rooms.get(roomId);
        room.leaveGame(playerID);
        room.leaveRoom(playerID);
    }

    public static RoomRefactored getRoom(UUID roomId) {
        return getInstance().rooms.get(roomId);
    }

    public static ArrayList<Lobby> getRoomsAsLobbies() {
        ArrayList<Lobby> lobbyList = new ArrayList<>();
        ConcurrentHashMap<UUID, Lobby> lobbyMap = getInstance().lobbies;

        for(UUID u : lobbyMap.keySet()) {
            lobbyList.add(lobbyMap.get(u));
        }

        return lobbyList;
    }

    //  Controlla che gli status tra lobby e room combacino. Restituisce true se entrambe sono aperte o entrambe chiuse,
    //  false altrimenti.
    private boolean checkIfStatusAreSync(RoomRefactored room, Lobby lobby) {
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
    private void synchronizeStatus(RoomRefactored room, Lobby lobby) {
        if(room.getRoomStatus().equals(RoomState.OPEN))
            lobby.setStatus(Lobby.LobbyStatus.OPEN);

        else
            lobby.setStatus(Lobby.LobbyStatus.CLOSED);
    }
}
