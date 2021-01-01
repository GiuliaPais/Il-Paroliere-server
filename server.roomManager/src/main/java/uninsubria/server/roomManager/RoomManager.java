package uninsubria.server.roomManager;

import uninsubria.server.services.api.AbstractServiceFactory;
import uninsubria.server.services.api.ServiceFactoryImpl;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.GameScore;

import java.io.IOException;
import java.time.Duration;
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
 * @version 0.9.3
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
	 * Add room proxy.
	 *
	 * @param playerWrapper the playerWrapper
	 * @throws IOException the io exception
	 */
	public synchronized void addRoomProxy(PlayerWrapper playerWrapper) throws IOException {
		ProxyRoom proxyRoom = new ProxyRoom(playerWrapper.getIpAddress());
		proxies.put(playerWrapper, proxyRoom);
	}

	/**
	 * Remove room proxy.
	 *
	 * @param playerWrapper the playerWrapper
	 */
	public synchronized void removeRoomProxy(PlayerWrapper playerWrapper) {
		try {
			proxies.remove(playerWrapper).quit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	public void newMatch(String[] gridF, Integer[] gridNum) {
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Void> task = () -> {
						entry.getValue().startNewMatch(gridF, gridNum);
						return null;
					};
					executorService.submit(task);
				});
	}

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
	}

	/**
	 * Legge le parole dai proxy e le restituisce insieme al giocatore che le ha trovate.
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
		return mapTmp;
	}


	public void sendScores(GameScore gameScore) {
		/* Create all the tasks */
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Void> task = () -> {
						entry.getValue().sendScores(gameScore);
						return null;
					};
					executorService.submit(task);
				});
	}

	public boolean setMatchTimeout(Duration waitTime) {
		List<Callable<Boolean>> tasks = new ArrayList<>();
		/* Create all the tasks */
		proxies.entrySet().stream()
				.forEach(entry -> {
					Callable<Boolean> task = () -> entry.getValue().setTimeoutMatch();
					tasks.add(task);
				});
		try {
			List<Future<Boolean>> results = executorService.invokeAll(tasks, waitTime.getSeconds(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void terminateGame() {

	}

	public Set<PlayerWrapper> getPlayers() {
		return proxies.keySet();
	}

	public void terminateManager() {
		for (ProxyRoom p : proxies.values()) {
			try {
				p.quit();
				p.terminate();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		executorService.shutdownNow();
	}

}
