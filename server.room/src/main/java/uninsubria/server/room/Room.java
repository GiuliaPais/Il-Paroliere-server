package uninsubria.server.room;


import uninsubria.server.match.Game;
import uninsubria.server.roomManager.RoomManager;
import uninsubria.server.roomReference.RoomState;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.chronometer.Counter;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the server-side implementation of a player lobby.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais (minor)
 * @version 0.9.3
 */
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

    /**
     * Instantiates a new Room.
     *
     * @param roomId     the room id
     * @param roomName   the room name
     * @param numPlayers the num players
     * @param language   the language
     * @param ruleset    the ruleset
     * @param creator    the creator
     */
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
        this.roomManager = new RoomManager();
        try {
            roomManager.addRoomProxy(creator.getIpAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lets the player join this room.
     *
     * @param player the player
     * @return the int
     */
    /*---Methods---*/
    public synchronized int joinRoom(PlayerWrapper player) {
        if (roomStatus.equals(RoomState.OPEN)) {
            try {
                roomManager.addRoomProxy(player.getIpAddress());
            } catch (IOException e) {
                return 1;
            }
            playerSlots.add(player);
            if(playerSlots.size() == numPlayers) {
                roomStatus = RoomState.FULL;
                ChronometerRoom cr = new ChronometerRoom(new Counter(0,10,0), id);
                cr.start();
            }
            return 0;
        } else {
            return 2;
        }
    }

    /**
     * Lets the player leave this room.
     *
     * @param playerID the player id
     */
    public synchronized void leaveRoom(String playerID) {
        List<PlayerWrapper> toRemove = new ArrayList<>();
        playerSlots.stream().filter(playerWrapper -> playerWrapper.getPlayer().getPlayerID().equals(playerID))
                .forEach(playerWrapper -> {
            toRemove.add(playerWrapper);
        });
        playerSlots.removeAll(toRemove);
        for (PlayerWrapper p : toRemove) {
            roomManager.removeRoomProxy(p.getIpAddress());
        }
        if (playerSlots.size() == 0) {
            RoomList.closeRoom(this.id);
            return;
        }
        if(playerSlots.size() < numPlayers)
            roomStatus = RoomState.OPEN;
    }

    /**
     * Gets room status.
     *
     * @return the room status
     */
    public RoomState getRoomStatus() {
        return roomStatus;
    }

    /**
     * Sets room status.
     *
     * @param roomStatus the room status
     */
    public void setRoomStatus(RoomState roomStatus) {
        this.roomStatus = roomStatus;
    }

    /**
     * Leave game.
     *
     * @param playerId the player id
     */
    public void leaveGame(String playerId) {

    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets room name.
     *
     * @return the room name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Gets ruleset.
     *
     * @return the ruleset
     */
    public Ruleset getRuleset() {
        return ruleset;
    }

    /**
     * Gets num players.
     *
     * @return the num players
     */
    public Integer getNumPlayers() {
        return numPlayers;
    }

    /**
     * New game.
     */
    public void newGame() {
        game = new Game();
    }

    /**
     * Gets player slots.
     *
     * @return the player slots
     */
    public ArrayList<PlayerWrapper> getPlayerSlots() {
        return playerSlots;
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

}
