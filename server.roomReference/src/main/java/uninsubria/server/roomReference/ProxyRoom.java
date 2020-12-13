package uninsubria.server.roomReference;

import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.utils.business.Player;
import uninsubria.utils.connection.*;
import uninsubria.utils.languages.Language;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ProxyRoom implements RoomManagerInterface {

	private static final int PORT = CommHolder.SERVER_PORT;

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private ObjectInput objectInput;
	private ObjectOutput objectOutput;
	private final Player player;
	private Language language;
	private PlayerScore playerScore;
	private Long ping;

	private final Long waitABitTime = 50L; // Tempo di attesa per il metodo privato waitABit. Richiamato anche in setSync.

	public ProxyRoom(Player player, Language language) {
		this.player = player;
		this.language = language;
		ping = 0L;
		setInOut();
	}

	/**
	 * Attende che vengano inviate le parole da analizzare.
	 */
	public void waitWords() {
		String action = "";

		try {
			action = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(action.equals(CommProtocolCommands.SEND_WORDS.getCommand()))
			receiveWords();
	}

	/**
	 * Manda al player identificato come server, il punteggio di tutti i giocatori sotto forma di array di stringhe
	 * anticipato dal tag "<SEND_SCORES>"
	 * @param scores l'array contenente i punteggi.
	 */
	@Override
	public void sendScores(PlayerScore[] scores) {
		out.println(CommProtocolCommands.SEND_SCORE.getCommand());
		String[] array = new String[scores.length];

		for(int i = 0; i < array.length; i++)
			array[i] = scores[i].toString();

		sendArray(array);
	}

	/**
	 * Manda al player identificato come server, la griglia sotto forma di array di stringhe anticipato dal tag "<GRID>".
	 * @param gridFaces le facce uscite sulla griglia da mandare.
	 * @param gridNumbers i numeri usciti sulla griglia da mandare.
	 */
	@Override
	public void sendGrid(String[] gridFaces, Integer[] gridNumbers) {
		out.println(CommProtocolCommands.SEND_GRID.getCommand());
		sendArray(gridFaces);
		waitABit();
		sendArray(gridNumbers);
	}

	/**
	 * Manda al player identificato come server, il tempo di attesa prima dell'inizio della partita.
	 */
	@Override
	public void setSyncTimer(Long millis) {
		out.println(CommProtocolCommands.SET_SYNC.getCommand());
		waitABit();
		Long timeToWait = millis - ping - waitABitTime; // Necessario sottrarre waitABit per eliminare la latenza del metodo.
		out.println(timeToWait + "");
	}

	/**
	 * Manda un ping al server del player per stabilire la latenza tra client del Room e player, settando il valore ping.
	 */
	@Override
	public void pingServer() {
		Long startTime = System.currentTimeMillis();
		String command = CommProtocolCommands.PING_SERVER.getCommand();
		out.println(command);

		try {
			if(in.readLine().equals(command)) {
				Long endTime = System.currentTimeMillis();
				ping = (endTime - startTime) /2;

			} else
				ping = 0L;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manda al player il nome del vincitore ed il suo punteggio.
	 * @param winner Il nome del vincitore.
	 * @param score Il suo punteggio.
	 */
	@Override
	public void endGame(String winner, int score) {
		out.println(CommProtocolCommands.END_GAME.getCommand());
		waitABit();
		out.println(winner + " " + score);
	}

	/**
	 * Chiude la connessione col giocatore.
	 */
	@Override
	public void close() {
		try {
			in.close();
			out.close();
			objectInput.close();
			objectOutput.close();
			socket.close();
		} catch (IOException ignored) { }
	}

	/**
	 * Ottiene le parole inviate dal player e le prepara per analizzarle.
	 */
	public void receiveWords() {
		String[] words = (String[]) readObject();
		playerScore = new PlayerScore(player, words, language);
	}

	public PlayerScore getPlayerScore() {
		return playerScore;
	}

	/**
	 * Restituisce il player associato al proxy.
	 * @return il player associato al proxy.
	 */
	public Player getPlayer() {
		return player;
	}

	public Long getPing() {
		return ping;
	}

	/*-----Private methods-----*/
	private void sendArray(Object[] array) {
		try {
			objectOutput.writeObject(array);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Object readObject() {
		Object obj = null;

		try {
			obj = objectInput.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return obj;
	}

	private void setInOut() {
		try {
			InetAddress tmpAddr = InetAddress.getByName(""); // Da rimuovere quando sarà implementato il metodo sotto
//			InetAddress playerAddr = player.getPlayerManager().getAddress();
			socket = new Socket(tmpAddr, PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			objectOutput = new ObjectOutputStream(socket.getOutputStream());
			objectInput = new ObjectInputStream((socket.getInputStream()));
		} catch(IOException ignored) { }
	}

	// Da inserire quando si mandano due o più stringhe di seguito per evitare che vengano mandate in un unico messaggio
	private void waitABit() {
		try {
			Thread.sleep(waitABitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
