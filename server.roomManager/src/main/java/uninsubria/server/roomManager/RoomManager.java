package uninsubria.server.roomManager;

import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.GameScore;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * RoomManager coordinates all operations that require the communication
 * of the room either with players (via socket) or with other server
 * modules.
 *
 * @author Davide di Giovanni
 * @author Giulia Pais
 * @version 0.9.7
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
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Instant> task = () -> entry.getValue().startNewGame();
					tasks.add(task);
					identity.put(tasks.indexOf(task), entry.getKey());
				});
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
					Set<PlayerWrapper> key = proxies.entrySet().stream()
							.filter(entry -> entry.getValue().equals(proxy))
							.map(entry -> entry.getKey())
							.collect(Collectors.toSet());
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
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Void> task = () -> {
						entry.getValue().startNewMatch(gridF, gridNum);
						return null;
					};
					futures.add(executorService.submit(task));
				});
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
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Void> task = () -> {
						entry.getValue().interruptGame();
						return null;
					};
					executorService.submit(task);
				});
	}

	/**
	 * Signals the players that the current game is ending.
	 */
	public synchronized void endGame() {
		/* Create all the tasks */
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Void> task = () -> {
						entry.getValue().endGame();
						return null;
					};
					executorService.submit(task);
				});
		//Game stats
		terminateManager();
	}

	/**
	 * Legge le parole dai proxy e le restituisce insieme al giocatore che le ha trovate.
	 *
	 * @return HashMap contenente player e parole trovate.
	 */
	public synchronized HashMap<PlayerWrapper, String[]> readWords() {
		HashMap<PlayerWrapper, String[]> mapTmp = new HashMap<>();
		List<Callable<ArrayList<String>>> tasks = new ArrayList<>();
		Map<Integer, PlayerWrapper> identity = new HashMap<>();
		/* Create all the tasks */
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<ArrayList<String>> task = () -> entry.getValue().readWords();
					tasks.add(task);
					identity.put(tasks.indexOf(task), entry.getKey());
				});
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
	 * Sets match timeout.
	 *
	 * @return the match timeout
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
	public void terminateManager() {
		executorService.shutdownNow();
	}

}
