package uninsubria.server.room;

import java.util.ArrayList;

import uninsubria.server.match.Game;
import uninsubria.utils.business.Player;
import uninsubria.server.roomReference.*;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

public class Room extends Thread{

	private RoomReference reference;
	private Game game;

	private ArrayList<Player> slots;
	private final int ID;

	/**
	 * La stanza viene creata nel momento in cui un player vi entra. Fintanto che la stanza esiste,
	 * sarà possibile per altri aggiugnervisi fintanto che non sarà raggiunto il numero massimo di player consentito.
	 * @param id l'id univoco della stanza.
	 * @param player il primo player ad entrare nella stanza, colui che ne richiede la creazione
	 */
	public Room(int id, Player player) {
		ID = id;
		reference = new RoomReference(player);
		this.start();
	}

	// Il trhead stanza esiste fintanto che al suo interno vi è almeno un player
	public void run() {
		while(reference.getActualPlayer() > 0) {


		}
	}

	/**
	 * Restituisce la lingua attualmente utilizzata. Di Default è italiano.
	 * @return la lingua attualmente utilizzata.
	 */
	public Language getLanguage() {
		return reference.getLanguage();
	}

	/**
	 * Setta una nuova lingua da utilizzare, passata come parametro.
	 * @param newLanguage la nuova lingua da utilizzare.
	 */
	public void setLanguage(Language newLanguage) {
		reference.setLanguage(newLanguage);
	}

	/**
	 * Permette ad un player, passato come parametro, di unirsi alla lobby in attesa dell'inizio della partita.
	 * Ciò è possibile esclusivamente se non è già stato raggiunto il numero massimo di player consentito.
	 * @param player il player che entra nella stanza e viene aggiunto alla coda di chi è già presente.
	 */
	public void joinRoom(Player player) {
		reference.joinRoom(player);
	}

	/**
	 * Restituisce il riferimento alla RoomReference attualmente in uso dalla Room.
	 * @return la RoomReference attualmente in uso dalla Room.
	 */
	public RoomReference getRoomReference() {
		return reference;
	}

	/**
	 * Permette di settare il numero massimo di giocatori col valore passato come parametro, purché sia un valore
	 * maggiore o uguale dell'attuale numero di giocatori presenti in stanza.
	 * I valori ammessi sono compresi tra 2 e 6. Valori minori o maggiori impostano il numero massimo di giocatori
	 * rispettivamente al minimo ed al massimo consentito.
	 * @param i il nuovo numero massimo di giocatori consentito.
	 */
	public void setMaxPlayer(int i) {
		reference.setMaxPlayer(i);
	}

	/**
	 Restituisce il numero massimo di player che la stanza può accettare.
	 * @return il numero di player massimo della stanza.
	 */
	public int getMaxPlayer() {
		return reference.getMaxPlayer();
	}

	/**
	 * Restituisce il numero attuale di player nella stanza
	 * @return il numero di player nella stanza
	 */
	public int getActualPlayer() {
		return reference.getActualPlayer();
	}

	/**
	 * Permette ad un player, passato come parametro, di uscire dalla lobby prima che la partita sia iniziata.
	 * @param player il player in uscita
	 */
	public void leaveRoom(Player player) {
		reference.leaveRoom(player);
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
		return reference.getRoomState();
	}

	/**
	 * Restituisce le attuali regole usate.
	 * @return RuleSet, le regole usate.
	 */
	public Ruleset getRuleSet() {
		return reference.getRuleSet();
	}

	/**
	 * Permette di settare le nuove regole, passate come parametro.
	 * @param ruleSet il nuovo set di regole.
	 */
	public void setRuleSet(Ruleset ruleSet) {
		reference.setRuleset(ruleSet);
	}

	/**
	 * Restituisce sotto forma di ArrayList tutti i player attualmente nella room.
	 * @return ArrayList\<Player\> attualmente nella room.
	 */
	public ArrayList<Player> getSlots() {
		return reference.getSlots();
	}

	/**
	 * Inizia una nuova partita istanziando il RoomManager per la gestione dei dati da mandare e ricevere
	 * ai player nella lobby.
	 */
	public void newGame() {
		if(reference.newGameIsPossible()) {
			game = new Game(reference);
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
		return reference.getRoomManager();
	}

	/**
	 * Distrugge immediatamente la stanza.
	 */
	public void interrupt() {
		super.interrupt();
		reference.setActualPlayer(0);
	}

	public String toString() {
		return "Room n-" + ID + ", actual player: " + getActualPlayer() + ", max player: " + getMaxPlayer() + ". ";
	}
}
