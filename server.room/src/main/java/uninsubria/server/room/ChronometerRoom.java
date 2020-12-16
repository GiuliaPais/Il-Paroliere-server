package uninsubria.server.room;

import uninsubria.server.roomReference.RoomState;
import uninsubria.utils.chronometer.Chronometer;
import uninsubria.utils.chronometer.Counter;

import java.util.UUID;

public class ChronometerRoom extends Chronometer {

    private UUID roomId;

    public ChronometerRoom(Counter counter, UUID roomId) {
        super(counter);
        this.roomId = roomId;
    }

    /**
     * Imposta lo status della stanza a TimeOut ed attende che il counter arrivi a zero. Se qualcuno abbandona la stanza prima
     * del termine, si interrompe immediatamente senza far partire il gioco. Se al termine del conteggio tutti i giocatori sono ancora
     * presenti, il gioco viene avviato.
     */
    public void run() {
        Room room = RoomList.getRoom(roomId);
        room.setRoomStatus(RoomState.TIMEOUT);

        while(!super.isInterrupted()) {

            if(room.getRoomStatus().equals(RoomState.OPEN))
                super.interrupt();

        }

        if(!room.getRoomStatus().equals(RoomState.OPEN)) {
            room.newGame();
            room.setRoomStatus(RoomState.GAMEON);
        }
    }
}
