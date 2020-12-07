package uninsubria.server.roomReference;

import uninsubria.server.scoreCounter.PlayerScore;

public interface RoomManagerInterface {

	int ROOM_PORT = 8889;

	void sendScores(PlayerScore[] scores);
	void sendGrid(String[] grid);
	void setSyncTimer(Long millis);
	void pingServer();
	void endGame(String winner, int score);
	void close();
}
