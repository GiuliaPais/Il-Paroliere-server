package uninsubria.server.room;

import uninsubria.server.match.Grid;
import uninsubria.utils.serviceResults.ServiceResultInterface;
import uninsubria.utils.business.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

public class ProxyRoom implements RoomManagerInterface {

	private static final int PORT = 9999;

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Player player;

	public ProxyRoom(Player player) {
		this.player = player;

		try {
			InetAddress tmpAddr = InetAddress.getByName(""); // Da rimuovere quando sarà implementato il metodo sotto
//			InetAddress playerAddr = player.getPlayerManager().getAddress();

			socket = new Socket(tmpAddr, PORT);
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
		out.println("<SYNC>");
		String serverTime = System.currentTimeMillis() + "";
		out.println(serverTime);

	}

	@Override
	public void synchronizeClocks() throws IOException {
		// TODO Auto-generated method stub
	}

}
