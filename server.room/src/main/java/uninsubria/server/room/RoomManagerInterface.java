package uninsubria.server.room;

import java.io.IOException;
import java.util.Map;

import uninsubria.server.match.Grid;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ServiceResultInterface;

public interface RoomManagerInterface {

	void sendScores(Map<Player, Integer> scores) throws IOException;
	void sendGrid(Grid grid) throws IOException;
	ServiceResultInterface areValidWord(String[] words) throws IOException;
	void setSyncTimer() throws IOException;
	void synchronizeClocks() throws IOException;
}
