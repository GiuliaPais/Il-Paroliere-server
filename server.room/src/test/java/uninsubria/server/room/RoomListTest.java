package uninsubria.server.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uninsubria.utils.business.Player;

import static org.junit.jupiter.api.Assertions.*;

class RoomListTest {

    private RoomList test;

    @BeforeEach
    void setUp() {
        test = RoomList.getInstance();
    }

    @Test
    void getRoomList() {
        assertEquals(0, test.getActualRooms());

        Player p1 = new Player();
        test.createRoom(p1);
        assertEquals(1, test.getActualRooms());
    }

    @Test
    void getActualRooms() {
        assertEquals(0, test.getActualRooms());

        Player p1 = new Player();
        test.createRoom(p1);
        assertEquals(1, test.getActualRooms());
    }

    @Test
    void getIdRoom() {
        assertEquals(0, test.getIdRoom());

        Player p1 = new Player();
        test.createRoom(p1);
        assertEquals(1, test.getIdRoom());

        Player p2 = new Player();
        test.createRoom(p2);
        assertEquals(2, test.getIdRoom());

        test.leaveRoom(2, p2);
        assertEquals(2, test.getIdRoom());

        test.leaveRoom(1, p1);
        assertEquals(0, test.getIdRoom());
    }

    @Test
    void joinRoom() {
        Player p1 = new Player();
        Player p2 = new Player();
        test.createRoom(p1);
        test.joinRoom(1, p2);

        assertEquals(2, test.getRoomList().get(1).getActualPlayer());
    }

    @Test
    void leaveRoom() {
        Player p1 = new Player();
        Player p2 = new Player();
        test.createRoom(p1);
        test.joinRoom(1, p2);
        test.leaveRoom(1, p1);

        assertEquals(1, test.getRoomList().get(1).getActualPlayer());
    }
}