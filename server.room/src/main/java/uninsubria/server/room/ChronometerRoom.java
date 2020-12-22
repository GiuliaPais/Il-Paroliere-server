package uninsubria.server.room;

import uninsubria.server.match.Game;
import uninsubria.server.match.GameState;
import uninsubria.server.roomManager.RoomManager;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.chronometer.Chronometer;
import uninsubria.utils.chronometer.Counter;

import java.util.HashMap;
import java.util.UUID;

public class ChronometerRoom extends Chronometer {

    private Room room;
    public RoomCommand command;
    private RoomManager roomManager;

    public ChronometerRoom(Counter counter, UUID roomId, RoomCommand command) {
        super(counter);
        room = RoomList.getRoom(roomId);
        this.command = command;
        roomManager = null;
    }

    public void run() {

        this.selectAction(command);

    }

    private void selectAction(RoomCommand action) {

        switch(action) {
            case START_NEW_GAME:
                this.startNewGame();
                break;

            case START_NEW_MATCH:
                this.startNewMatch();
                break;

            case CONCLUDE_MATCH:
                this.concludeMatch();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    /**
     * Imposta lo status della stanza a TimeOut ed attende che il counter arrivi a zero. Fintanto che il cronometro è
     * attivo, sarà impossibile per i player abbandonare la stanza. Una volta iniziato il game,
     * sarà possibile abbandonarlo.
     */
    private void startNewGame() {
        room.setRoomStatus(RoomState.TIMEOUT);
        room.setIsPossibleToLeave(false);

        super.run();

        room.newGame();
        room.setRoomStatus(RoomState.GAMEON);
        room.setIsPossibleToLeave(true);
        room.newMatch();
    }

    private void startNewMatch() {
        Game game = room.getGame();

        if(game.getGameState().equals(GameState.ONGOING)) {
            game.newMatch();
            String[] faces = game.getActualMatch().getGrid().getDiceFaces();
            Integer[] numbs = game.getActualMatch().getGrid().getDiceNumb();

            // Far mandare a room Manager i dati

            super.run();

            HashMap<PlayerWrapper, String[]> words = room.getRoomManager().readWords();
            game.calculateMatchScore(words);
            room.concludeMatch();
        }
    }

    private void concludeMatch() {
        Game game = room.getGame();
        game.ConcludeMatchAndCalculateTotalScore();

        // String[] matchScores = game.getActualMatch().getPlayersScore();
        // String[] totalScores = game.getTotalPlayersScore();
        // Far mandare i punteggi al RoomManager

        super.run();

        if(game.getGameState().equals(GameState.ONGOING))
            room.newMatch();
    }

}
