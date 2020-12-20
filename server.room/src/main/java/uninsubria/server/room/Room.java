package uninsubria.server.room;


import uninsubria.server.match.Game;
import uninsubria.server.roomManager.RoomManager;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.util.ArrayList;
import java.util.UUID;

public class Room {

    /*---Fields---*/
    private UUID id;
    private String roomName;
    private Integer numPlayers;
    private Language language;
    private Ruleset ruleset;
    private ArrayList<PlayerWrapper> playerSlots;
    private RoomState roomStatus;
    private Game game;
    private RoomManager roomManager;
    private boolean isPossibleToLeave;

    /*---Constructors---*/
    public Room(UUID roomId, String roomName, Integer numPlayers, Language language, Ruleset ruleset,
                PlayerWrapper creator) {

        this.id = roomId;
        this.roomName = roomName;
        this.language = language;
        this.ruleset = ruleset;
        this.roomStatus = RoomState.OPEN;

        setNumPlayers(numPlayers);

        this.playerSlots = new ArrayList<>();
        this.playerSlots.add(creator);
        isPossibleToLeave = true;
    }

    /*---Methods---*/
    public synchronized boolean joinRoom(PlayerWrapper player) {
        boolean entered = false;

        if (roomStatus.equals(RoomState.OPEN)) {
            playerSlots.add(player);
            entered = true;
        }

        if(playerSlots.size() == numPlayers) {
            // ISTANZIARE ROOM MANAGER
            roomStatus = RoomState.FULL;
            ChronometerRoom chronometerRoom = new ChronometerRoom(ruleset.getTimeToStart(), id,
                    RoomCommand.START_NEW_GAME);

            chronometerRoom.start();
        }

        return entered;
    }

    public void leaveRoom(String playerID) {
        if(isPossibleToLeave) {
            PlayerWrapper playerTmp = this.findById(playerID);
            playerSlots.remove(playerTmp);

            if (playerSlots.size() < numPlayers)
                roomStatus = RoomState.OPEN;
        }
    }

    public RoomState getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomState roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void leaveGame(String playerID) {
        if(game != null) {
            PlayerWrapper playerTmp = this.findById(playerID);
            game.abandon(playerTmp);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public Language getLanguage() {
        return language;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public Integer getNumPlayers() {
        return numPlayers;
    }

    public void newGame() {
        game = new Game(playerSlots, language, ruleset);
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<PlayerWrapper> getPlayerSlots() {
        return playerSlots;
    }

    public void setIsPossibleToLeave(boolean isPossible) {
        isPossibleToLeave = isPossible;
    }

    public boolean isPossibleToLeave() {
        return isPossibleToLeave;
    }

    /*---Private methods---*/
    private void setNumPlayers(int numPlayers) {
        if(numPlayers < 2)
            this.numPlayers = 2;

        else if(numPlayers > 6)
            this.numPlayers = 6;

        else
            this.numPlayers = numPlayers;
    }

    private PlayerWrapper findById(String playerID) {
        PlayerWrapper playerTmp = null;

        for (int i = 0; i < playerSlots.size(); i++) {
            String id = playerSlots.get(i).getPlayerID();

            if (playerID.equals(id))
                playerTmp = playerSlots.get(i);
        }

        return playerTmp;
    }

}
