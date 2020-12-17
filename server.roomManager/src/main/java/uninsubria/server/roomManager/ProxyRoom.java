package uninsubria.server.roomManager;


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
import java.util.Objects;

/**
 * Proxy class for the room. Responsible for communication over socket.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais
 * @version 0.9.1
 */
public class ProxyRoom implements ProxySkeletonInterface, RoomProxyInterface {

	private final Duration FIXED_TIME_ADJUST = Duration.ofMillis(100);
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
//	private Long ping;
//
//	private final Long waitABitTime = 50L; // Tempo di attesa per il metodo privato waitABit. Richiamato anche in setSync.

	public ProxyRoom(InetAddress address) throws IOException {
		this.socket = new Socket(address, CommHolder.ROOM_PORT);
		this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	@Override
	public void setTimer(TimerType timerType) throws IOException {
		Instant initial = Instant.now();
		writeCommand(CommProtocolCommands.SET_SYNC, timerType);
		try {
			if (in == null) {
				this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			}
			readCommand(in.readUTF());
			Instant after = Instant.now();
			long ping = initial.until(after, ChronoUnit.MILLIS);
			Instant future = Instant.now().plus(ping, ChronoUnit.MILLIS).plus(FIXED_TIME_ADJUST);
			out.writeObject(future);
			//other stuff TODO
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
			case SET_SYNC -> {}
		}
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