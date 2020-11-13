package uninsubria.server.room;

import java.util.ArrayList;

import uninsubria.server.match.Game;
import uninsubria.server.match.GameState;
import tmpClasses.*;

public class Room extends Thread{

	private RuleSet ruleSet;
	private ArrayList<Player> slots;
	private RoomManager manager;
	private RoomState state;
	private Game game;

	private int id;

	private static int MaxPlayer = 6;
	private static int actualPlayer = 0;

	/**
	 * La stanza viene creata nel momento in cui un player vi entra. Fintanto che vi sia almeno un player,
	 * sarà possibile per altri aggiugnervisi fintanto che non sarà raggiunto il numero massimo consentito.
	 * @param player
	 */
	public Room(int id, Player player) {
		this.id = id;
		state = RoomState.OPEN;
		slots = new ArrayList<Player>();

		this.joinRoom(player);
		this.start();
	}

	// Il trhead stanza esiste fintanto che al suo interno vi è almeno un player
	public void run() {
		while(actualPlayer > 0) {

		}
	}

	/**
	 * Permette ad un player, passato come parametro, di unirsi alla lobby in attesa dell'inizio della partita.
	 * Ciò è possibile esclusivamente se non è già stato raggiunto il numero massimo di player consentito.
	 * @param player
	 */
	public void joinRoom(Player player) {
		if(actualPlayer < MaxPlayer) {
			slots.add(player);
			actualPlayer++;
		}

		if(actualPlayer == MaxPlayer) {
			state = RoomState.FULL;
		}
	}


	/**
	 * Restituisce il numero attuale di player nella stanza
	 * @return il numero di player nella stanza
	 */
	public int getActualPlayer() {
		return actualPlayer;
	}

	/**
	 * Permette ad un player, passato come parametro, di uscire dalla lobby prima che la partita sia iniziata.
	 * @param player in uscita
	 */
	public void leaveRoom(Player player) {
		slots.remove(player);
		actualPlayer--;
		state = RoomState.OPEN;
	}

	/**
	 * Restituisce l'id della stanza.
	 * @return int
	 */
	public int getIdRoom() {
		return id;
	}

	/**
	 * Restituisce lo stato attuale della stanza.
	 * @return RoomState, lo stato attuale.
	 */
	public RoomState getRoomState() {
		return state;
	}

	/**
	 * Restituisce le attuali regole usate.
	 * @return RuleSet, le regole usate.
	 */
	public RuleSet getRuleSet() {
		return ruleSet;
	}

	/**
	 * Permette di settare le nuove regole, passate come parametro.
	 * @param ruleSet
	 */
	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	/**
	 * Restituisce sotto forma di ArrayList tutti i player attualmente nella room.
	 * @return ArrayList\<Player\> attualmente nella room.
	 */
	public ArrayList<Player> getSlots() {
		return slots;
	}

	/**
	 * Inizia una nuova partita istanziando il RoomManager per la gestione dei dati da mandare e ricevere
	 * ai player nella lobby.
	 */
	public void newGame() {
		if(actualPlayer == MaxPlayer) {
			Player[] slotsArray = new Player[slots.size()];
			slots.toArray(slotsArray);
			manager = new RoomManager(slotsArray);
			game = new Game(slots);
			state = RoomState.GAMEON;
		}
	}

	public Game getGame() {
		return game;
	}

	/**
	 * Restituisce l'attuale RoomManager se istanziato. Null altrimenti.
	 * @return RoomManager.
	 */
	public RoomManager getRoomManager() {
		return manager;
	}

	/**
	 * Distrugge immediatamente la stanza.
	 */
	public void stopped() {
		actualPlayer = 0;
	}

	public String toString() {
		return "Room " + id + ", " + "Actual Player: " + actualPlayer + ", " + state.toString() + ".";
	}
}
