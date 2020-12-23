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

	/**
	 * Manda ai player le facce dei dadi usciti dalla griglia ed i relativi numeri di dado.
	 * @param faces le facce dei dadi.
	 * @param numbs i numeri dei dadi.
	 */
	public void sendGrid(String[] faces, Integer[] numbs) {
		Set<Map.Entry<PlayerWrapper, ProxyRoom>> proxySet =  proxies.entrySet();

		for(Map.Entry<PlayerWrapper, ProxyRoom> entry : proxySet) {
			try {
				entry.getValue().sendGrid(faces, numbs);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Manda lo score del match e lo score dell'intero game, estraendo dal PlayerWrapper solo il nome del giocatore.
	 * @param matchScores lo score del match.
	 * @param gameScores lo score del game.
	 */
	public void sendScores(HashMap<PlayerWrapper, Integer> matchScores, HashMap<PlayerWrapper, Integer> gameScores) {
		// Estrazione dei nomi dal PlayerWrapper
		HashMap<String, Integer> matchTmp = new HashMap<>();
		HashMap<String, Integer> gameTmp = new HashMap<>();

		Set<Map.Entry<PlayerWrapper, Integer>> matchSet =  matchScores.entrySet();
		Set<Map.Entry<PlayerWrapper, Integer>> gameSet =  gameScores.entrySet();

		for(Map.Entry<PlayerWrapper, Integer> entry : matchSet) {
			matchTmp.put(entry.getKey().getPlayer().getName(), entry.getValue());
		}

		for(Map.Entry<PlayerWrapper, Integer> entry : gameSet) {
			matchTmp.put(entry.getKey().getPlayer().getName(), entry.getValue());
		}

		// Invio dei punteggi
		Set<Map.Entry<PlayerWrapper, ProxyRoom>> proxySet =  proxies.entrySet();

		for(Map.Entry<PlayerWrapper, ProxyRoom> entry : proxySet) {
			try {
				entry.getValue().sendScores(matchTmp, gameTmp);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


//
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
//
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

}
