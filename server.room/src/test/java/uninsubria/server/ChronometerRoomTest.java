package uninsubria.server;

import uninsubria.server.room.ChronometerRoom;
import uninsubria.server.room.RoomCommand;
import uninsubria.utils.chronometer.Counter;

import java.util.UUID;

public class ChronometerRoomTest {

    public static void main(String[] args) {

        Counter c = new Counter(0, 5, 0);
        UUID id = UUID.randomUUID();
        RoomCommand command = RoomCommand.TEST;

        ChronometerRoom cr = new ChronometerRoom(c, id, command);
        cr.start();
    }
}
