package uninsubria.server.room;

import uninsubria.utils.chronometer.Chronometer;
import uninsubria.utils.chronometer.Counter;

import java.util.UUID;

public class ChronometerRoom extends Chronometer {

    private Room room;
    public RoomCommand command;

    public ChronometerRoom(Counter counter, UUID roomId, RoomCommand command) {
        super(counter);
        room = RoomList.getRoom(roomId);
        this.command = command;
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
                break;

            case TEST:
                System.out.println("INIZIA IL TEST");
                Long start = System.currentTimeMillis();
                super.run();

                System.out.println("FINITO IL TEST");
                Long end = System.currentTimeMillis();
                Long time = end - start;
                System.out.println("Tempo trascorso: " + time);
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
    }

}
