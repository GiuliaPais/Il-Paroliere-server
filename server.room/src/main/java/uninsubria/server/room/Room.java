package uninsubria.server.room;


import uninsubria.server.match.Game;
import uninsubria.server.roomManager.RoomManager;
import uninsubria.server.wrappers.PlayerWrapper;
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
    private boolean isPossibleToLeave;
    private ChronometerRoom chronometerRoom;

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

        isPossibleToLeave = true;
        roomManager = new RoomManager();

        try {
            roomManager.addRoomProxy(creator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*---Methods---*/
    /**
     * Lets the player join this room.
     *
     * @param player the player
     * @return the int
     */
    public synchronized int joinRoom(PlayerWrapper player) {
        if (roomStatus.equals(RoomState.OPEN)) {
            try {
                roomManager.addRoomProxy(player);
            } catch (IOException e) {
                return 1;
            }
            playerSlots.add(player);
            if(playerSlots.size() == numPlayers) {
                roomStatus = RoomState.FULL;
                chronometerRoom = new ChronometerRoom(ruleset.getTimeToStart(), id,
                        RoomCommand.START_NEW_GAME);
                chronometerRoom.start();
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
        if(isPossibleToLeave) {
            List<PlayerWrapper> toRemove = new ArrayList<>();
            playerSlots.stream().filter(playerWrapper -> playerWrapper.getPlayer().getPlayerID().equals(playerID))
                    .forEach(playerWrapper -> {
                        toRemove.add(playerWrapper);
                    });
            playerSlots.removeAll(toRemove);
            for (PlayerWrapper p : toRemove) {
                roomManager.removeRoomProxy(p);
            }
            if (playerSlots.size() == 0) {
                RoomList.closeRoom(this.id);
                return;
            }
            if (playerSlots.size() < numPlayers)
                roomStatus = RoomState.OPEN;
        }
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
     * Permette al giocatore passato come parametro di abbandonare la partita.
     * @param playerID il player del giocatore.
     */
    public void leaveGame(String playerID) {
        if(game != null) {
            PlayerWrapper playerTmp = this.findById(playerID);
            game.abandon(playerTmp);

            if(ruleset.interruptIfSomeoneLeaves())
                chronometerRoom.interrupt();
        }
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
     * Inizia un nuovo game.
     */
    public void newGame() {
        game = new Game(playerSlots, language, ruleset);
    }

    /**
     * Inizia un nuovo match.
     */
    public void newMatch() {
        chronometerRoom = new ChronometerRoom(ruleset.getTimeToMatch(), id, RoomCommand.START_NEW_MATCH);
        chronometerRoom.start();
    }

    /**
     * Conclude l'attuale match e calcola i punteggi.
     */
    public void concludeMatch() {
        chronometerRoom = new ChronometerRoom(ruleset.getTimeToWaitFromMatchToMatch(), id, RoomCommand.CONCLUDE_MATCH);
        chronometerRoom.start();
    }

    /**
     * Restituisce il RoomManager.
     * @return il RoomManager.
     */
    public RoomManager getRoomManager() {
        return roomManager;
    }

    /**
     * Restituisce il Game.
     * @return il Game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets player slots.
     *
     * @return the player slots
     */
    public ArrayList<PlayerWrapper> getPlayerSlots() {
        return playerSlots;
    }

    /**
     * Setta la possibilità di abbandonare la Room.
     * @param isPossible booleano per settare la possibilità di abbandono.
     */
    public void setIsPossibleToLeave(boolean isPossible) {
        isPossibleToLeave = isPossible;
    }

    /**
     * Restituisce true se è possibile abbandonare la stanza, false altrimenti.
     * @return booleano che stabilisce se è possibile abbandonare.
     */
    public boolean isPossibleToLeave() {
        return isPossibleToLeave;
    }

    /**
     * Restituisce gli attuali player nella Room.
     * @return gli attuali player nella room.
     */
    public ArrayList<String> getCurrentPlayers() {
        ArrayList<String> playerNames = new ArrayList<>();
        playerSlots.stream()
                .map(playerWrapper -> playerWrapper.getPlayerID())
                .forEach(id -> playerNames.add(id));
        return playerNames;
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
