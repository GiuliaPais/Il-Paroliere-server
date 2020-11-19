package uninsubria.server.roomReference;

import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class RoomManager implements RoomManagerInterface {

	private Player[] players;
	private ProxyRoom[] proxy;
	private final boolean exists;

	public RoomManager(ArrayList<Player> players) {
		setPlayers(players);
		setProxy();
		exists = true;
	}

	@Override
	public void sendScores(Map<Player, Integer> scores) throws IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * Manda ai player la griglia sotto forma di stringa anticipato dal tag "<GRID>".
	 * @param grid la stringa da mandare
	 * @throws IOException
	 */
	@Override
	public void sendGrid(String grid) throws IOException {
		for(ProxyRoom p : proxy)
			p.sendGrid(grid);
	}

	@Override
	public ServiceResultInterface areValidWord(String[] words) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Manda ai player il proprio system.currentTimeMillis() sotto forma
	 * di stringa anticipato dal tag "<SYNC>" per l'operazione di sincronizzazione.
	 * @throws IOException
	 */
	@Override
	public void setSyncTimer() throws IOException {
		for(ProxyRoom p : proxy)
			p.setSyncTimer();
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

	/**
	 * Restituisce true se il RoomManager è stato istanziato.
	 * @return true se istanziato correttamente.
	 */
	public boolean exists() {
		return exists;
	}

	// Genera i proxy necessari per la comunicazione col singolo player.
	private void setProxy() {
		proxy = new ProxyRoom[players.length];
		for(int i = 0; i < players.length; i++) {
			proxy[i] = new ProxyRoom(players[i]);
		}
	}

	// Trasforma l'ArrayList di player in un semplice array.
	private void setPlayers(ArrayList<Player> p) {
		players = new Player[p.size()];

		for(int i = 0; i < p.size(); i++)
			players[i] = p.get(i);
	}

}
