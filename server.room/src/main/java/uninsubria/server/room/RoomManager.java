package uninsubria.server.room;

import java.io.IOException;
import java.util.Map;

import uninsubria.server.match.Grid;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ServiceResultInterface;

public class RoomManager implements RoomManagerInterface {

	private Player[] players;
	private ProxyRoom[] proxy;

	public RoomManager(Player[] players) {
		this.players = players;

		setProxy(players);
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

	/**
	 * Restituisce i player attualmente presenti in stanza.
	 * @return array di Player.
	 */
	public Player[] getPlayers() {
		return players;
	}

	// Genera i proxy necessari per la comunicazione col singolo player.
	private void setProxy(Player[] players) {

		proxy = new ProxyRoom[players.length];
		for(int i = 0; i < players.length; i++) {
			proxy[i] = new ProxyRoom(players[i]);
		}
	}

}
