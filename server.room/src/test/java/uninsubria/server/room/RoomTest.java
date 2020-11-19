package uninsubria.server.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uninsubria.server.match.GameState;
import uninsubria.server.roomReference.RoomState;
import uninsubria.utils.business.Player;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room test;

    @BeforeEach
    void setUp() {
        Player p1 = new Player();
        test = new Room(1, p1);
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
        test.newGame();
        assertEquals(RoomState.GAMEON, test.getRoomState());
    }

    @Test
    void getRuleSet() {
    }

    @Test
    void setRuleSet() {
    }

    @Test
    void getGame() {
        assertEquals(null, test.getGame());

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
        test.newGame();

        assertEquals(true, test.getGame().exists());
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
        test.newGame();

        assertEquals(true, test.getRoomManager().exists());
    }

    @Test
    void RoomGameInteraction() {
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
        test.newGame();

        assertEquals(6, test.getActualPlayer());

        test.getGame().abandon(p6);
        assertEquals(5, test.getActualPlayer());

        assertEquals(false, test.getRoomReference().newGameIsPossible());
    }
}