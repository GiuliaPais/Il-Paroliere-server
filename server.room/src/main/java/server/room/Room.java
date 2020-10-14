package server.room;

import java.util.ArrayList;

import tmpClasses.*;

public class Room extends Thread{

	private RuleSet ruleSet;
	private ArrayList<Player> slots;
	private RoomManager manager;
	private RoomState state;
	private Player roomAdmin;
	
	private static int MaxPlayer = 6;
	private static int actualPlayer = 0;
	
	/**
	 * Quando un player crea una nuova stanza viene nominato Player Principale. Fintanto che sarà presente,
	 * altri player fino al numero massimo consentito potranno unirsi alla lobby.
	 * @param player
	 */
	public Room(Player player) {
		state = RoomState.OPEN;
		slots = new ArrayList<Player>();
		roomAdmin = player;
		join(roomAdmin);
	}
	
	// Il trhead stanza esiste fintanto che al suo interno vi è l'admin della stanza.
	public void run() {
		
		while(actualPlayer > 0) {
			
		}
		
	}
	
	/**
	 * Permette ad un player, passato come parametro, di unirsi alla lobby in attesa dell'inizio della partita.
	 * Ciò è possibile esclusivamente se non è già stato raggiunto il numero massimo di player consentito.
	 * @param player
	 */
	public void join(Player player) {
		if(actualPlayer < MaxPlayer) {
			slots.add(player);
			actualPlayer++;
		}
		
		if(actualPlayer == MaxPlayer) {
			state = RoomState.FULL;
		}
	}
	
	/**
	 * Permette ad un player, passato come parametro, di uscire dalla lobby prima che la partita sia iniziata.
	 * Qualora il player in uscita fosse il creatore della stanza, tutti i giocatori vengono espulsi e la 
	 * stanza distrutta.
	 * @param player
	 */
	public void leave(Player player) {
		if(player != roomAdmin) {
			slots.remove(player);
			actualPlayer--;
			state = RoomState.OPEN;
		} else {
			for(int i = 0; i < actualPlayer; i++) {
				slots.remove(i);
				actualPlayer--;
			}
		}
	}
	
	// Serve davvero?
	/**
	 * Setta lo stato attuale della stanza.
	 * @param state
	 */
	public void setStates(RoomState state) {
		this.state = state;
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
			state = RoomState.GAMEON;
		}
	}
	
	/**
	 * Restituisce l'attuale RoomManager se istanziato. Null altrimenti.
	 * @return RoomManager.
	 */
	public RoomManager getRoomManager() {
		return manager;
	}
}
