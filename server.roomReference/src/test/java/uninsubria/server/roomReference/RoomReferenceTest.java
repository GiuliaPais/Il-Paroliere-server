package uninsubria.server.roomReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uninsubria.utils.business.Player;

import static org.junit.jupiter.api.Assertions.*;

class RoomReferenceTest {

    private RoomReference test;

    @BeforeEach
    void setUp() {
        Player p = new Player();
        test = new RoomReference(p);
    }

    @Test
    void joinRoom() {
        assertEquals(1, test.getActualPlayer());

        Player p2 = new Player();
        test.joinRoom(p2);
        assertEquals(2, test.getActualPlayer());
    }

    @Test
    void setMaxPlayer() {
        test.setMaxPlayer(1);
        assertEquals(2, test.getMaxPlayer());

        test.setMaxPlayer(10);
        assertEquals(6, test.getMaxPlayer());

        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();
        test.joinRoom(p2);
        test.joinRoom(p3);
        test.joinRoom(p4);
        test.setMaxPlayer(4);
        assertEquals(4, test.getMaxPlayer());

        test.setMaxPlayer(2);
        assertEquals(4, test.getMaxPlayer());
    }

    @Test
    void leaveRoom() {
        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();
        test.joinRoom(p2);
        test.joinRoom(p3);
        test.joinRoom(p4);
        assertEquals(4, test.getActualPlayer());

        test.leaveRoom(p3);
        assertEquals(3, test.getActualPlayer());

        test.leaveRoom(p2);
        assertEquals(2, test.getActualPlayer());
    }

    @Test
    void getRoomState() {
        assertEquals(RoomState.OPEN, test.getRoomState());

        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();
        Player p5 = new Player();
        Player p6 = new Player();
        test.joinRoom(p2);
        test.joinRoom(p3);
        test.joinRoom(p4);
        test.joinRoom(p5);
        test.joinRoom(p6);
        assertEquals(RoomState.FULL, test.getRoomState());

        test.leaveRoom(p5);
        assertEquals(RoomState.OPEN, test.getRoomState());

        test.joinRoom(p5);
        test.newGameIsPossible();
        assertEquals(RoomState.GAMEON, test.getRoomState());
    }

    @Test
    void newGameIsPossible() {
        assertEquals(false, test.newGameIsPossible());

        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();
        Player p5 = new Player();
        Player p6 = new Player();
        test.joinRoom(p2);
        test.joinRoom(p3);
        test.joinRoom(p4);
        assertEquals(false, test.newGameIsPossible());

        test.setMaxPlayer(4);
        assertEquals(true, test.newGameIsPossible());

        test.setMaxPlayer(6);
        test.joinRoom(p5);
        test.joinRoom(p6);
        assertEquals(true, test.newGameIsPossible());
    }

    @Test
    void getRoomManager() {
        assertEquals(null, test.getRoomManager());

        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();
        Player p5 = new Player();
        Player p6 = new Player();
        test.joinRoom(p2);
        test.joinRoom(p3);
        test.joinRoom(p4);
        test.joinRoom(p5);
        test.joinRoom(p6);
        test.newGameIsPossible();

        assertEquals(true, test.getRoomManager().exists());
    }
}