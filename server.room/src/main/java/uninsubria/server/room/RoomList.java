package uninsubria.server.room;

import java.util.HashMap;

import tmpClasses.Player;

public class RoomList {

	private static final int MAX_ROOMS = 10;
	private static RoomList instance;

	private HashMap<Integer, Room> rooms;
	private int idRoom, actualRooms;

	/**
	 * Istanza privata, necessario usare il metodo getInstance().
	 */
	private RoomList() {
		idRoom = 0;
		actualRooms = 0;
		rooms = new HashMap<Integer, Room>();
	}

	/**
	 * Istanza un'unica RoomList senza la quale non è possibile eseguire i metodi.
	 * @return RoomList
	 */
	public static RoomList getInstance() {
		instance = new RoomList();
		return instance;
	}

	/**
	 * Restituisce l'elenco di tutte le stanze attualmente esistenti.
	 * @return Map<Integer, Room>
	 */
	public synchronized HashMap<Integer, Room> getRoomList() {
		return rooms;
	}

	/**
	 * Restituisce il numero delle attuali stanze esistenti.
	 * @return int
	 */
	public synchronized int getActualRooms() {
		return actualRooms;
	}

	/**
	 * Restituisce il numero dell'ultimo id utilizzato per la creazione di una stanza.
	 * @return l'ultimo id utilizzato come int.
	 */
	public synchronized int getIdRoom() {
		return idRoom;
	}

	/**
	 * Crea una nuova stanza dal parametro Player passatogli, aumentando il conteggio delle stanze attuali
	 * e generando un nuovo id per le stanze successive.
	 * @param player
	 */
	public void createRoom(Player player) {
		if(actualRooms < MAX_ROOMS) {
			this.incrementRoom();
			Room tmp = new Room(idRoom, player);
			rooms.put(idRoom, tmp);
		}
	}

	/**
	 * Permette al player passato come secondo parametro di unirsi alla stanza il cui id è uguale a quello passato
	 * come primo parametro.
	 * @param id
	 * @param player
	 */
	public synchronized void joinRoom(int id, Player player) {
		if(rooms.containsKey(id)) {
			rooms.get(id).joinRoom(player);
		}
	}

	/**
	 * Permette al player passato come secondo parametro di abbandonare la stanza il cui id è uguale al primo
	 * parametro. Qualora il player fosse l'admin della stanza, la stanza verrà distrutta e rimossa dalla lista
	 * di stanze accessibili. Se non esistono più stanze, resetta gli id disponibili da assegnare.
	 * @param id
	 * @param player
	 */
	public synchronized void leaveRoom(int id, Player player) {
		Room tmp = rooms.get(id);
		tmp.leaveRoom(player);

		if(tmp.getActualPlayer() == 0) {
			rooms.remove(id);
			this.decrementRoom();
		}
	}

	// Incrementa di 1 l'id room e setta le stanze attuali
	private synchronized void incrementRoom() {
		idRoom++;
		actualRooms++;
	}

	// Decrementa di 1 le attuali stanze e controlla se resettare gli id
	private synchronized void decrementRoom() {
		actualRooms--;
		this.resetId();
	}

	// resetta l'id room se e solo se non esistono più stanze esistenti
	private synchronized void resetId() {
		if(actualRooms == 0)
			idRoom = 0;
	}
}

	public synchronized void joinRoom(int id, Player player) {
		if(rooms.containsKey(id)) {
			rooms.get(id).joinRoom(player);
		}
	}
	
	/**
	 * Permette al player passato come secondo parametro di abbandonare la stanza il cui id è uguale al primo
	 * parametro. Qualora il player fosse l'admin della stanza, la stanza verrà distrutta e rimossa dalla lista
	 * di stanze accessibili. Se non esistono più stanze, resetta gli id disponibili da assegnare.
	 * @param id
	 * @param player
	 */
	public synchronized void leaveRoom(int id, Player player) {
		Room tmp = rooms.get(id);
		tmp.leaveRoom(player);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!tmp.isAlive()) {
			rooms.remove(id);
			this.decrementRoom();
		}
	}
	
	// Incrementa di 1 l'id room e setta le stanze attuali
	private synchronized void incrementRoom() {
		idRoom++;
		actualRooms++;
	}
	
	// Decrementa di 1 le attuali stanze e controlla se resettare gli id
	private synchronized void decrementRoom() {
		actualRooms--;
		this.resetId();
	}
	
	// resetta l'id room se e solo se non esistono più stanze esistenti
	private synchronized void resetId() {
		if(actualRooms == 0)
			idRoom = 0;
	}
}
