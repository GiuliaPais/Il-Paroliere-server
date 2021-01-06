package uninsubria.server.roomManager;

import uninsubria.server.match.Match;
import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.services.roomServiceType.RoomServiceType;
import uninsubria.server.wrappers.GameEntriesWrapper;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.WordRequest;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * RoomManager coordinates all operations that require the communication
 * of the room either with players (via socket) or with other server
 * modules.
 *
 * @author Davide di Giovanni
 * @author Giulia Pais
 * @version 0.9.10
 */
public class RoomManager {

	private final AbstractServiceFactory serviceFactory;
	private Map<PlayerWrapper, ProxyRoom> proxies;
	private ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * Instantiates a new Room manager.
	 */
	public RoomManager() {
		this.serviceFactory = new ServiceFactoryImpl();
		this.proxies = new HashMap<>();
	}

	/*---Methods---*/
	/**
	 * Tries to establish a connection with the given player by instantiating a proxy.
	 *
	 * @param playerWrapper the playerWrapper - which contains reference to the player and its IP address
	 * @throws IOException if the opening of the socket fails
	 */
	public synchronized void addRoomProxy(PlayerWrapper playerWrapper) throws IOException {
		ProxyRoom proxyRoom = new ProxyRoom(playerWrapper.getIpAddress());
		proxies.put(playerWrapper, proxyRoom);
	}

	/**
	 * Signals all players in the room that a new game is starting.
	 *
	 * @return A list of Instant objects representing the time
	 * at which the timers will start
	 */
	public synchronized List<Instant> newGame() {
		List<Instant> times = new ArrayList<>();
		List<Callable<Instant>> tasks = new ArrayList<>();
		Map<Integer, PlayerWrapper> identity = new HashMap<>();
		/* Create all the tasks */
		for (Map.Entry<PlayerWrapper, ProxyRoom> playerWrapperProxyRoomEntry : proxies.entrySet()) {
			Callable<Instant> task = () -> playerWrapperProxyRoomEntry.getValue().startNewGame();
			tasks.add(task);
			identity.put(tasks.indexOf(task), playerWrapperProxyRoomEntry.getKey());
		}
		/* Submit */
		try {
			List<Future<Instant>> results = executorService.invokeAll(tasks);
			for (Future<Instant> future : results) {
				try {
					Instant instant = future.get();
					times.add(instant);
				} catch (ExecutionException e) {
					/* If there were communication errors, this player must be disconnected */
					e.printStackTrace();
					ProxyRoom proxy = proxies.get(identity.get(results.indexOf(future)));
					proxy.terminate();
					Set<PlayerWrapper> key = new HashSet<>();
					for (Map.Entry<PlayerWrapper, ProxyRoom> entry : proxies.entrySet()) {
						if (entry.getValue().equals(proxy)) {
							PlayerWrapper entryKey = entry.getKey();
							key.add(entryKey);
						}
					}
					for (PlayerWrapper k : key) {
						proxies.remove(k);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return times;
	}

	/**
	 * Signals the players a new match is starting, sending the
	 * match grid.
	 *
	 * @param gridF   the faces of dices in the grid
	 * @param gridNum the nuber of the dices in the grid
	 */
	public boolean newMatch(String[] gridF, Integer[] gridNum) {
		List<Future<Void>> futures = new ArrayList<>();
		/* Create all the tasks */
		for (Map.Entry<PlayerWrapper, ProxyRoom> entry : proxies.entrySet()) {
			Callable<Void> task = () -> {
				entry.getValue().startNewMatch(gridF, gridNum);
				return null;
			};
			futures.add(executorService.submit(task));
		}
		for (Future<Void> f : futures) {
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Signals players that the current game has been interrupted.
	 */
	public synchronized void interruptGame() {
		/* Create all the tasks */
		proxies.values().stream().<Callable<Void>>map(proxyRoom -> () -> {
			proxyRoom.interruptGame();
			return null;
		}).forEach(task -> executorService.submit(task));
	}

	/**
	 * Signals the players that the current game is ending and registers game stats.
	 */
	public synchronized void endGame(List<String> totalGameGrid, Ruleset ruleset, Language language, Integer players, ArrayList<Match> matches) {
		List<Future<HashSet<WordRequest>>> futures = new ArrayList<>();
		HashSet<WordRequest> requested = new HashSet<>();
		for (Map.Entry<PlayerWrapper, ProxyRoom> entry : proxies.entrySet()) {
			Callable<HashSet<WordRequest>> task = () -> {
				if (!Thread.currentThread().isInterrupted()) {
					return entry.getValue().endGame();
				}
				return null;
			};
			futures.add(executorService.submit(task));
		}
		for (Future<HashSet<WordRequest>> future : futures) {
			HashSet<WordRequest> playerReq = null;
			try {
				playerReq = future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			requested.addAll(playerReq);
		}
		GameEntriesWrapper gw = new GameEntriesWrapper(matches, requested);
		Service service = serviceFactory.getService(RoomServiceType.GAME_STATS, UUID.randomUUID(), totalGameGrid.toArray(new String[0]), players, ruleset, language, gw);
		Thread thread = new Thread(() -> service.execute());
		thread.start();
	}

	/**
	 * Requests the list of words found by each player at the end of a match.
	 *
	 * @return a map with every player and the words found
	 */
	public synchronized HashMap<PlayerWrapper, String[]> readWords() {
		HashMap<PlayerWrapper, String[]> mapTmp = new HashMap<>();
		List<Callable<ArrayList<String>>> tasks = new ArrayList<>();
		Map<Integer, PlayerWrapper> identity = new HashMap<>();
		/* Create all the tasks */
		for (Map.Entry<PlayerWrapper, ProxyRoom> entry : proxies.entrySet()) {
			Callable<ArrayList<String>> task = () -> entry.getValue().readWords();
			tasks.add(task);
			identity.put(tasks.indexOf(task), entry.getKey());
		}
		/* Submit */
		try {
			List<Future<ArrayList<String>>> results = executorService.invokeAll(tasks);
			for (Future<ArrayList<String>> future : results) {
				try {
					ArrayList<String> words = future.get();
					String[] arrayWords = new String[0];
					arrayWords = words.toArray(arrayWords);
					mapTmp.put(identity.get(results.indexOf(future)), arrayWords);
				} catch (ExecutionException e) {
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return mapTmp;
	}


	/**
	 * Sends the scores to the players.
	 *
	 * @param gameScore the game score
	 */
	public void sendScores(GameScore gameScore) {
		for (ProxyRoom proxy : proxies.values()) {
			try {
				proxy.sendScores(gameScore);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets match timeout and waits for all players to notify they're ready.
	 */
	public void setMatchTimeout() {
		List<Thread> threads = new ArrayList<>();
		for (ProxyRoom proxy : proxies.values()) {
			Thread thread = new Thread(() -> {
				try {
					proxy.setTimeoutMatch();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
			threads.add(thread);
			thread.start();
		}
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets players.
	 *
	 * @return the players
	 */
	public Set<PlayerWrapper> getPlayers() {
		return proxies.keySet();
	}

	/**
	 * Terminate manager.
	 */
	public void terminateManager() throws IOException {
		for (ProxyRoom proxyRoom : proxies.values()) {
			proxyRoom.quit();
		}
		executorService.shutdownNow();
	}

}
