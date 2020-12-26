package uninsubria.server.roomManager;


import uninsubria.server.wrappers.PlayerWrapper;
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
import java.util.HashMap;
import java.util.Objects;

/**
 * Proxy class for the room. Responsible for communication over socket.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais
 * @version 0.9.3
 */
public class ProxyRoom implements ProxySkeletonInterface, RoomProxyInterface {

	private final Duration FIXED_TIME_ADJUST = Duration.ofMillis(2000);
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private PlayerWrapper playerWrapper;

	private Instant pingInit;
	private Instant nextScheduledTimerTime;

	public ProxyRoom(PlayerWrapper playerWrapper) throws IOException {
		this.playerWrapper = playerWrapper;
		this.socket = new Socket(playerWrapper.getIpAddress(), CommHolder.ROOM_PORT);
		this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	@Override
	public Instant startNewGame(String[] faces, Integer[] numbs) throws IOException, ClassNotFoundException {
		/* Signals a new game is starting by sending the grid and uses this to ping the client */
		pingInit = Instant.now();
		writeCommand(CommProtocolCommands.GAME_STARTING, faces, numbs);
		if (in == null) {
			this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		}
		readCommand(in.readUTF());
		return nextScheduledTimerTime;
	}

	@Override
	public void interruptGame() throws IOException {
		writeCommand(CommProtocolCommands.INTERRUPT_GAME);
	}

	@Override
	public void quit() throws IOException {
		writeCommand(CommProtocolCommands.QUIT);
		terminate();
	}

	@Override
	public void sendScores(HashMap<String, Integer> matchScores, HashMap<String, Integer> gameScores) throws IOException {
		writeCommand(CommProtocolCommands.SEND_SCORE, matchScores, gameScores);
	}

	@Override
	public void writeCommand(CommProtocolCommands command, Object... params) throws IOException {
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
		}
	}

	@Override
	public String[] readWords() throws IOException {
		writeCommand(CommProtocolCommands.SEND_WORDS);
		this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		String[] words = null;

		try {
			words = (String[]) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return words;
	}

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

//	/**
//	 * Attende che vengano inviate le parole da analizzare.
//	 */
//	public void waitWords() {
//		String action = "";
//
//		try {
//			action = in.readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		if(action.equals(CommProtocolCommands.SEND_WORDS.getCommand()))
//			receiveWords();
//	}
//
//	/**
//	 * Manda al player identificato come server, il punteggio di tutti i giocatori sotto forma di array di stringhe
//	 * anticipato dal tag "<SEND_SCORES>"
//	 * @param scores l'array contenente i punteggi.
//	 */
//	@Override
//	public void sendScores(PlayerScore[] scores) {
//		out.println(CommProtocolCommands.SEND_SCORE.getCommand());
//		String[] array = new String[scores.length];
//
//		for(int i = 0; i < array.length; i++)
//			array[i] = scores[i].toString();
//
//		sendArray(array);
//	}
//
//	/**
//	 * Manda al player identificato come server, la griglia sotto forma di array di stringhe anticipato dal tag "<GRID>".
//	 * @param gridFaces le facce uscite sulla griglia da mandare.
//	 * @param gridNumbers i numeri usciti sulla griglia da mandare.
//	 */
//	@Override
//	public void sendGrid(String[] gridFaces, Integer[] gridNumbers) {
//		out.println(CommProtocolCommands.SEND_GRID.getCommand());
//		sendArray(gridFaces);
//		waitABit();
//		sendArray(gridNumbers);
//	}
//
//	/**
//	 * Manda al player identificato come server, il tempo di attesa prima dell'inizio della partita.
//	 */
//	@Override
//	public void setSyncTimer(Long millis) {
//		out.println(CommProtocolCommands.SET_SYNC.getCommand());
//		waitABit();
//		Long timeToWait = millis - ping - waitABitTime; // Necessario sottrarre waitABit per eliminare la latenza del metodo.
//		out.println(timeToWait + "");
//	}
//
//	/**
//	 * Manda un ping al server del player per stabilire la latenza tra client del Room e player, settando il valore ping.
//	 */
//	@Override
//	public void pingServer() {
//		Long startTime = System.currentTimeMillis();
//		String command = CommProtocolCommands.PING_SERVER.getCommand();
//		out.println(command);
//
//		try {
//			if(in.readLine().equals(command)) {
//				Long endTime = System.currentTimeMillis();
//				ping = (endTime - startTime) /2;
//
//			} else
//				ping = 0L;
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Manda al player il nome del vincitore ed il suo punteggio.
//	 * @param winner Il nome del vincitore.
//	 * @param score Il suo punteggio.
//	 */
//	@Override
//	public void endGame(String winner, int score) {
//		out.println(CommProtocolCommands.END_GAME.getCommand());
//		waitABit();
//		out.println(winner + " " + score);
//	}
//
//	/**
//	 * Chiude la connessione col giocatore.
//	 */
//	@Override
//	public void close() {
//		try {
//			in.close();
//			out.close();
//			objectInput.close();
//			objectOutput.close();
//			socket.close();
//		} catch (IOException ignored) { }
//	}
//
//	/**
//	 * Ottiene le parole inviate dal player e le prepara per analizzarle.
//	 */
//	public void receiveWords() {
//		String[] words = (String[]) readObject();
//		playerScore = new PlayerScore(player, words, language);
//	}
//
//	public PlayerScore getPlayerScore() {
//		return playerScore;
//	}
//
//	/**
//	 * Restituisce il player associato al proxy.
//	 * @return il player associato al proxy.
//	 */
//	public Player getPlayer() {
//		return player;
//	}
//
//	public Long getPing() {
//		return ping;
//	}
//
//	/*-----Private methods-----*/
//	private void sendArray(Object[] array) {
//		try {
//			objectOutput.writeObject(array);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Object readObject() {
//		Object obj = null;
//
//		try {
//			obj = objectInput.readObject();
//		} catch (IOException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		return obj;
//	}
//
//	private void setInOut() {
//		try {
//			InetAddress tmpAddr = InetAddress.getByName(""); // Da rimuovere quando sarà implementato il metodo sotto
////			InetAddress playerAddr = player.getPlayerManager().getAddress();
//			socket = new Socket(tmpAddr, PORT);
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//			objectOutput = new ObjectOutputStream(socket.getOutputStream());
//			objectInput = new ObjectInputStream((socket.getInputStream()));
//		} catch(IOException ignored) { }
//	}
//
//	// Da inserire quando si mandano due o più stringhe di seguito per evitare che vengano mandate in un unico messaggio
//	private void waitABit() {
//		try {
//			Thread.sleep(waitABitTime);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

}
