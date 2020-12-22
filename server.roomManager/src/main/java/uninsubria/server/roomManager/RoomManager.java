package uninsubria.server.roomManager;

import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.wrappers.PlayerWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * RoomManager coordinates all operations that require the communication
 * of the room either with players (via socket) or with other server
 * modules.
 *
 * @author Davide di Giovanni
 * @author Giulia Pais
 * @version 0.9.2
 */
public class RoomManager {

	private final AbstractServiceFactory serviceFactory;
	private Map<PlayerWrapper, ProxyRoom> proxies;

	/**
	 * Instantiates a new Room manager.
	 */
	public RoomManager() {
		this.serviceFactory = new ServiceFactoryImpl();
		this.proxies = new HashMap<PlayerWrapper, ProxyRoom>();
	}

	/*---Methods---*/
	/**
	 * Add room proxy.
	 *
	 * @param playerWrapper the playerWrapper
	 * @throws IOException the io exception
	 */
	public void addRoomProxy(PlayerWrapper playerWrapper) throws IOException {
		ProxyRoom proxyRoom = new ProxyRoom(playerWrapper);
		proxies.put(playerWrapper, proxyRoom);
	}

	/**
	 * Remove room proxy.
	 *
	 * @param playerWrapper the playerWrapper
	 */
	public void removeRoomProxy(PlayerWrapper playerWrapper) {
		try {
			proxies.remove(playerWrapper).quit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Legge le parole dai proxy e le restituisce insieme al giocatore che le ha trovate.
	 * @return HashMap contenente player e parole trovate.
	 */
	public HashMap<PlayerWrapper, String[]> readWords() {
		HashMap<PlayerWrapper, String[]> mapTmp = new HashMap<>();
		Set<Map.Entry<PlayerWrapper, ProxyRoom>> proxySet =  proxies.entrySet();

		for(Map.Entry<PlayerWrapper, ProxyRoom> entry : proxySet) {
			try {
				mapTmp.put(entry.getKey(), entry.getValue().readWords());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return mapTmp;
	}


//	@Override
//	public void sendScores(PlayerScore[] scores) {
//		for(ProxyRoom p : proxy)
//			p.sendScores(scores);
//
//	}
//
//	/**
//	 * Manda ai player la griglia sotto forma di stringa anticipato dal tag "<GRID>".
//	 * @param gridFaces le facce uscite sulla griglia da mandare.
//	 * @param gridNumbers i numeri usciti sulla griglia da mandare.
//	 */
//	@Override
//	public void sendGrid(String[] gridFaces, Integer[] gridNumbers) {
//		for(ProxyRoom p : proxy)
//			p.sendGrid(gridFaces, gridNumbers);
//	}
//
//	/**
//	 * Manda ai player il proprio system.currentTimeMillis() sotto forma
//	 * di stringa anticipato dal tag "<SYNC>" per l'operazione di sincronizzazione.
//	 */
//	@Override
//	public void setSyncTimer(Long millis) {
//		for(ProxyRoom p : proxy)
//			p.setSyncTimer(millis);
//	}
//
//	/**
//	 * Setta la latenza dei proxy tra player e room.
//	 */
//	@Override
//	public void pingServer() {
//		for(ProxyRoom p : proxy)
//			p.pingServer();
//	}
//
//	/**
//	 * Manda a tutti i giocatori il nome del vincitore ed il suo punteggio.
//	 * @param winner il nome del vincitore.
//	 * @param score il punteggio del vincitore.
//	 */
//	@Override
//	public void endGame(String winner, int score) {
//		for(ProxyRoom p : proxy)
//			p.endGame(winner, score);
//	}
//
//	/**
//	 * Chiude tutte le connessioni coi giocatori.
//	 */
//	@Override
//	public void close() {
//		for(ProxyRoom p : proxy)
//			p.close();
//	}
//
//	/**
//	 * Restituisce i player attualmente presenti in stanza.
//	 * @return array di Player.
//	 */
//	public Player[] getPlayers() {
//		return players;
//	}
//
//	/**
//	 * Restituisce true se il RoomManager è stato istanziato.
//	 * @return true se istanziato correttamente.
//	 */
//	public boolean exists() {
//		return exists;
//	}
//
//	/**
//	 * Restituisce l'attuale lingua utilizzata.
//	 * @return la lingua utilizzata.
//	 */
//	public Language getLanguage() {
//		return language;
//	}
//
//	/**
//	 * Attende che vengano inviate le parole alla fine del match.
//	 */
//	public void waitWords() {
//		for(ProxyRoom p : proxy)
//			p.waitWords();
//	}
//
//	/**
//	 * Restituisce i PlayerScore di tutti i player come array.
//	 * @return PlayerScore come array.
//	 */
//	public PlayerScore[] getPlayersScore() {
//		PlayerScore[] array = new PlayerScore[proxy.length];
//
//		for(int i = 0; i < array.length; i++)
//			array[i] = proxy[i].getPlayerScore();
//
//		return array;
//	}
//
//	/*---Private methods---*/
//	// Genera i proxy necessari per la comunicazione col singolo player.
//	private void setProxy() {
//		proxy = new ProxyRoom[players.length];
//
//		for(int i = 0; i < players.length; i++) {
//			proxy[i] = new ProxyRoom(players[i], language);
//		}
//	}

}
