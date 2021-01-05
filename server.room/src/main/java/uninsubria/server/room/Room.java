package uninsubria.server.room;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uninsubria.server.room.game.Game;
import uninsubria.server.room.game.GameState;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Represents the server-side implementation of a player lobby.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais
 * @version 0.9.7
 */
public class Room {

    /*---Fields---*/
    private final UUID id;
    private final String roomName;
    private final Integer numPlayers;
    private final Language language;
    private final Ruleset ruleset;
    private final ArrayList<PlayerWrapper> playerSlots;
    private final ObjectProperty<RoomState> roomStatus;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> currentGame;
    private RoomLeaveMonitor monitor;

    public enum RoomState {
        OPEN, FULL, GAMEON
    }

    /*---Constructors---*/
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
    public Room(UUID roomId, String roomName, Integer numPlayers, Language language, Ruleset ruleset,
                PlayerWrapper creator) {
        this.id = roomId;
        this.roomName = roomName;
        this.language = language;
        this.ruleset = ruleset;
        this.roomStatus = new SimpleObjectProperty<>(RoomState.OPEN);
        this.numPlayers = numPlayers;
        this.playerSlots = new ArrayList<>();
        this.playerSlots.add(creator);
        this.monitor = new RoomLeaveMonitor();
        setStatusListeners();
    }

    /*---Methods---*/
    /**
     * Lets the player join this room.
     *
     * @param player the player
     * @return the int
     */
    public synchronized int joinRoom(PlayerWrapper player) {
        if (getRoomStatus().equals(RoomState.OPEN)) {
            playerSlots.add(player);
            if(playerSlots.size() == numPlayers) {
                setRoomStatus(RoomState.FULL);
            }
            return 0;
        } else if (getRoomStatus().equals(RoomState.FULL)){
            return 1;
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
        playerSlots.removeIf(pw -> pw.getPlayerID().equals(playerID));
        if (playerSlots.size() < numPlayers & !roomStatus.equals(RoomState.GAMEON))
            setRoomStatus(RoomState.OPEN);
    }

    public RoomState getRoomStatus() {
        return roomStatus.get();
    }

    public ObjectProperty<RoomState> roomStatusProperty() {
        return roomStatus;
    }

    public synchronized void setRoomStatus(RoomState roomStatus) {
        this.roomStatus.set(roomStatus);
    }

    public synchronized void leaveGame(String playerID) {
        if (ruleset.interruptIfSomeoneLeaves()) {
            /* Sends an interrupt to the game if only if the ruleset contains this rule */
            currentGame.cancel(true);
        }
        PlayerWrapper pw = findById(playerID);
        playerSlots.remove(pw);
        /* Notifies the game someone has left the game causing the thread to awake */
        monitor.signalPlayerisLeaving(pw);
    }

    public synchronized void interruptGame() {
        if (currentGame != null) {
            currentGame.cancel(true);
        }
        if (playerSlots.size() < numPlayers) {
            setRoomStatus(RoomState.OPEN);
        } else {
            setRoomStatus(RoomState.FULL);
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
    /**
     * Starts a new game when the room is full.
     * If any problem occurs while trying to communicate with
     * players, rooms adjusts according to the Ruleset chosen:
     * for standard ruleset the game is interrupted and unreachable
     * players are expelled from the room.
     */
    private void newGame() {
        monitor.clearQueue();
        Game game = new Game(playerSlots, ruleset, language, id, monitor);
        game.gameStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(GameState.INTERRUPTED)) {
                interruptGame();
                return;
            }
            if (newValue.equals(GameState.FINISHED)) {
                if (playerSlots.size() < numPlayers) {
                    setRoomStatus(RoomState.OPEN);
                } else {
                    setRoomStatus(RoomState.FULL);
                }
            }
        });
        currentGame = executorService.submit(game);
        setRoomStatus(RoomState.GAMEON);
    }

    private void setStatusListeners() {
        roomStatus.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FULL -> newGame();
            }
        });
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
