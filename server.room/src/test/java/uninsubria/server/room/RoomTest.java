package uninsubria.server.room;

import server.room.Room;
import tmpClasses.Player;

public class RoomTest {

	public static void main(String[] args) {
		
		Player player1 = new Player();
		Player player2 = new Player();
		
		Room room = new Room(1, player1);
		System.out.println(room.getActualPlayer());
		
		room.joinRoom(player2);
		System.out.println(room.getActualPlayer());
		
		room.leaveRoom(player1);
		System.out.println(room.getActualPlayer());
		
		room.leaveRoom(player2);

	}

}
