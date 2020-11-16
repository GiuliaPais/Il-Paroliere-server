package uninsubria.server.room;

import java.util.ArrayList;

import uninsubria.server.match.Game;
import uninsubria.utils.business.Player;
import tmpClasses.RuleSet;

public class Room extends Thread{

	private RuleSet ruleSet;
	private RoomManager manager;
	private RoomState state;
	private Game game;
	private int maxPlayer;
	private int actualPlayer;

	private ArrayList<Player> slots;
	private final int ID;

	/**
	 * La stanza viene creata nel momento in cui un player vi entra. Fintanto che la stanza esiste,
	 * sarà possibile per altri aggiugnervisi fintanto che non sarà raggiunto il numero massimo di player consentito.
	 * @param id l'id univoco della stanza.
	 * @param player il primo player ad entrare nella stanza, colui che ne richiede la creazione
	 */
	public Room(int id, Player player) {
		this.ID = id;
		state = RoomState.OPEN;
		slots = new ArrayList<>();
		maxPlayer = 6;
		actualPlayer = 0;

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
	 * @param player il player che entra nella stanza e viene aggiunto alla coda di chi è già presente.
	 */
	public void joinRoom(Player player) {
		if(actualPlayer < maxPlayer) {
			slots.add(player);
			actualPlayer++;
		}

		if(actualPlayer == maxPlayer) {
			state = RoomState.FULL;
		}
	}

	/**
	 * Permette di settare il numero massimo di giocatori col valore passato come parametro, purché sia un valore
	 * maggiore o uguale dell'attuale numero di giocatori presenti in stanza.
	 * I valori ammessi sono compresi tra 2 e 6. Valori minori o maggiori impostano il numero massimo di giocatori
	 * rispettivamente al minimo ed al massimo consentito.
	 * @param i il nuovo numero massimo di giocatori consentito.
	 */
	public void setMaxPlayer(int i) {
		if(actualPlayer <= i) {
			if (maxPlayer < 2)
				maxPlayer = 2;
			else if (maxPlayer > 6)
				maxPlayer = 6;
			else
				maxPlayer = i;
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
	 * @param player il player in uscita
	 */
	public void leaveRoom(Player player) {
		slots.remove(player);
		actualPlayer--;
		state = RoomState.OPEN;
	}

	/**
	 * Restituisce l'id della stanza.
	 * @return l'id della stanza
	 */
	public int getIdRoom() {
		return ID;
	}

	/**
	 * Restituisce lo stato attuale della stanza.
	 * @return RoomState, lo stato attuale della stanza.
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
	 * @param ruleSet il nuovo set di regole.
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
		if(actualPlayer == maxPlayer) {
			Player[] slotsArray = new Player[slots.size()];
			slots.toArray(slotsArray);
			manager = new RoomManager(slotsArray);
			game = new Game(slots);
			state = RoomState.GAMEON;
		}
	}

	/**
	 * Restituisce un riferimento al gioco in corso.
	 * @return Game, il game in corso.
	 */
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
	public void interrupt() {
		super.interrupt();
		actualPlayer = 0;
	}
}
