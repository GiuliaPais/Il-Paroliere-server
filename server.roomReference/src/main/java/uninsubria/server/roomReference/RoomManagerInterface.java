package uninsubria.server.roomReference;

import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.IOException;
import java.util.Map;

public interface RoomManagerInterface {

	void sendScores(Map<Player, Integer> scores) throws IOException;
	void sendGrid(String grid) throws IOException;
	ServiceResultInterface areValidWord(String[] words) throws IOException;
	void setSyncTimer() throws IOException;
	void synchronizeClocks(int m, int s, int ml) throws IOException;
}
