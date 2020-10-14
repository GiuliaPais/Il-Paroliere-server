package server.room;

import java.io.IOException;
import java.util.Map;

import tmpClasses.*;

public interface RoomManagerInterface {

	public void sendScores(Map<Player, Integer> scores) throws IOException;
	public void sendGrid(Grid grid) throws IOException;
	public ServiceResultInterface areValidWord(String[] words) throws IOException;
	public void setSyncTimer() throws IOException;
	public void synchronizeClocks() throws IOException;
}
