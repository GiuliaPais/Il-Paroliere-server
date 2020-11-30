package uninsubria.server.roomReference;

import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.utils.business.Player;
import uninsubria.utils.chronometer.Chronometer;
import uninsubria.utils.chronometer.Counter;
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

	public ProxyRoom(Player player, Language language) {
		this.player = player;
		this.language = language;
		setInOut();
	}

	public void waitWords() {
		String action = null;

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
	 * @param grid l'array da mandare
	 */
	@Override
	public void sendGrid(String[] grid) {
		out.println(CommProtocolCommands.SEND_GRID.getCommand());
		sendArray(grid);
	}

	/**
	 * Manda al player identificato come server, il proprio system.currentTimeMillis() sotto forma
	 * di stringa anticipato dal tag "<SYNC>" per l'operazione di sincronizzazione.
	 */
	@Override
	public void setSyncTimer() {
		out.println(CommProtocolCommands.SET_SYNC.getCommand());
		waitABit();
		out.println(System.currentTimeMillis() + "");
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
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
