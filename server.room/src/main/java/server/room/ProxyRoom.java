package server.room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import server.match.Grid;
import tmpClasses.Player;
import uninsubria.utils.serviceResults.ServiceResultInterface;

public class ProxyRoom implements RoomManagerInterface {

	private static final int PORT = 9999;
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Player player;
	
	public ProxyRoom(Player player) {
		this.player = player;
	
		try {
			socket = new Socket(player.getAddr(), PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);	
		} catch(IOException e) { }
	}
	
	@Override
	public void sendScores(Map<Player, Integer> scores) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendGrid(Grid grid) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public ServiceResultInterface areValidWord(String[] words) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSyncTimer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void synchronizeClocks() throws IOException {
		// TODO Auto-generated method stub

	}

}
