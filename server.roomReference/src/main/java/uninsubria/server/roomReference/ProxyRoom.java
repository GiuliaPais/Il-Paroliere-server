package uninsubria.server.roomReference;

import uninsubria.utils.business.Player;
import uninsubria.utils.chronometer.Chronometer;
import uninsubria.utils.chronometer.Counter;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

public class ProxyRoom implements RoomManagerInterface {

	private static final int PORT = 8888;

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

	/**
	 * Manda al player identificato come server, la griglia sotto forma di stringa anticipato dal tag "<GRID>".
	 * @param grid la stringa da mandare
	 * @throws IOException
	 */
	@Override
	public void sendGrid(String grid) throws IOException {
		out.println("<GRID>" + grid);
	}

	@Override
	public ServiceResultInterface areValidWord(String[] words) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Manda al player identificato come server, il proprio system.currentTimeMillis() sotto forma
	 * di stringa anticipato dal tag "<SYNC>" per l'operazione di sincronizzazione.
	 * @throws IOException
	 */
	@Override
	public void setSyncTimer() throws IOException {
		out.println("<SYNC>" + System.currentTimeMillis());
	}

	@Override
	public void synchronizeClocks(int m, int s, int ml) throws IOException {
		Counter c = new Counter(m, s, ml);
		Chronometer chron = new Chronometer(c);
		try {
			chron.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
