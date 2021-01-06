package uninsubria.server.roomManager;


import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.WordRequest;
import uninsubria.utils.connection.CommHolder;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;
import uninsubria.utils.managersAPI.RoomProxyInterface;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Proxy class for the room. Responsible for communication over socket.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais
 * @version 0.9.11
 */
public class ProxyRoom implements ProxySkeletonInterface, RoomProxyInterface {

	private final Duration FIXED_TIME_ADJUST = Duration.ofMillis(3000);
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private Instant pingInit;
	private Instant nextScheduledTimerTime;
	private List<Object> receivedObjectQueue;

	/**
	 * Instantiates a new Proxy room.
	 *
	 * @param address the address
	 * @throws IOException the io exception
	 */
	public ProxyRoom(InetAddress address) throws IOException {
		this.socket = new Socket(address, CommHolder.ROOM_PORT);
		this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		this.receivedObjectQueue = new ArrayList<>();
	}

	@Override
	public Instant startNewGame() throws IOException, ClassNotFoundException {
		pingInit = Instant.now();
		writeCommand(CommProtocolCommands.GAME_STARTING);
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		readCommand(in.readUTF());
		return nextScheduledTimerTime;
	}

	@Override
	public void startNewMatch(String[] faces, Integer[] numbs) throws IOException, ClassNotFoundException {
		writeCommand(CommProtocolCommands.NEW_MATCH, faces, numbs);
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		readCommand(in.readUTF());
	}

	@Override
	public void interruptGame() throws IOException {
		writeCommand(CommProtocolCommands.INTERRUPT_GAME);
	}

	@Override
	public HashSet<WordRequest> endGame() throws IOException, ClassNotFoundException {
		writeCommand(CommProtocolCommands.END_GAME);
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		readCommand(in.readUTF());
		HashSet<WordRequest> requestedWords = (HashSet<WordRequest>) receivedObjectQueue.remove(receivedObjectQueue.size()-1);
		return requestedWords;
	}

	@Override
	public void quit() throws IOException {
		writeCommand(CommProtocolCommands.QUIT);
		terminate();
	}

	@Override
	public void sendScores(GameScore gameScores) throws IOException {
		writeCommand(CommProtocolCommands.SEND_SCORE, gameScores.getMatchWords(), gameScores.getScores(), gameScores.getWinner());
	}

	@Override
	public synchronized void writeCommand(CommProtocolCommands command, Object... params) throws IOException {
		out.writeUTF(command.getCommand());
		for (Object p : params) {
			if (p instanceof String) {
				String s = (String) p;
				out.writeUTF(s);
			} else {
				out.writeObject(p);
			}
		}
		out.flush();
	}

	@Override
	public void readCommand(String command) throws IOException, ClassNotFoundException {
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
		switch (Objects.requireNonNull(com)) {
			case GAME_STARTING -> {
				Instant pingReceived = Instant.now();
				long ping = pingInit.until(pingReceived, ChronoUnit.MILLIS);
				nextScheduledTimerTime = Instant.now().plus(ping, ChronoUnit.MILLIS).plus(FIXED_TIME_ADJUST);
				setTimer(nextScheduledTimerTime);
			}
			case SEND_WORDS -> {
				ArrayList<String> words = (ArrayList<String>) in.readObject();
				receivedObjectQueue.add(words);
			}
			case TIMEOUT_MATCH, NEW_MATCH -> {return;}
			case END_GAME -> {
				HashSet<WordRequest> res = (HashSet<WordRequest>) in.readObject();
				receivedObjectQueue.add(res);
			}
		}
	}

	@Override
	public ArrayList<String> readWords() throws IOException{
		writeCommand(CommProtocolCommands.SEND_WORDS);
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		ArrayList<String> receivedWords = new ArrayList<>();
		try {
			readCommand(in.readUTF());
			receivedWords = (ArrayList<String>) receivedObjectQueue.remove(receivedObjectQueue.size()-1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return receivedWords;
	}

	@Override
	public boolean setTimeoutMatch() throws IOException, ClassNotFoundException {
		writeCommand(CommProtocolCommands.TIMEOUT_MATCH);
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		readCommand(in.readUTF());
		return true;
	}

	/**
	 * Terminates the proxy by closing all resources.
	 */
	public void terminate() {
		try {
			if (in != null)
				in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setTimer(Instant instant) throws IOException {
		writeCommand(CommProtocolCommands.SET_SYNC, instant);
	}

}
