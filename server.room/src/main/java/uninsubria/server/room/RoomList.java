package uninsubria.server.room;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

/**
 * Class implementing a list of rooms.
 * Rooms can be created, deleted and joined by players until the maximum amount of rooms supported
 * has been reached.
 * The implementation of RoomList is thread-safe and changes to the underlying map can be observed
 * through the use of an appropriate Listener.
 *
 * @author Davide Di Giovanni, Giulia Pais (minor changes)
 *
 * @version 0.9.1
 */
public class RoomList {

	private ServerUDP serverUDP;
//	private static final int MAX_ROOMS = 10;
	private static RoomList instance;

	private ConcurrentHashMap<UUID, Room> rooms;
	private int idRoom, actualRooms;

	/**
	 * Istanza privata, necessario usare il metodo getInstance().
	 */
	private RoomList() {
		idRoom = 0;
		actualRooms = 0;
		rooms = new ConcurrentHashMap<>();
		serverUDP = new ServerUDP();
	}

	/**
	 * Gets the only instance of RoomList available. If there is no instance active, a new instance is created.
	 *
	 * @return the RoomList object
	 */
	public static RoomList getInstance() {
		if (instance == null) {
			instance = new RoomList();
		}
		return instance;
	}

//	/**
//	 * Restituisce l'elenco di tutte le stanze attualmente esistenti.
//	 * @return Map<Integer, Room>
//	 */
//	public ConcurrentHashMap<UUID, Room> getRoomList() {
//		return rooms;
//	}

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
	public void createRoom(PlayerWrapper player, Lobby lobby) {
//		if (roomsFull()) {
//			//send errors
//			return;
//		}
		getInstance().incrementRoom();
		Room tmp = new Room(player, lobby.getRoomName(), lobby.getNumPlayers(), lobby.getLanguage(), lobby.getRuleset());
		getInstance().rooms.put(lobby.getRoomId(), tmp);
	}

	/**
	 * Permette al player passato come secondo parametro di unirsi alla stanza il cui id è uguale a quello passato
	 * come primo parametro.
	 * @param id
	 * @param player
	 */
	public synchronized void joinRoom(PlayerWrapper player, UUID id) {
		//Questo metodo va sistemato perchè va chiamato da un servizio con i parametri appropriati,
		//serve classe adapter per player contenente l'ip
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
		//Questo metodo va sistemato perchè va chiamato da un servizio con i parametri appropriati,
		//serve classe adapter per player contenente l'ip
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

	public static ArrayList<Lobby> getRoomsAsLobbies() {
		ArrayList<Lobby> lobbies = new ArrayList<>();
		for (UUID key : getInstance().rooms.keySet()) {
			Room room = getInstance().rooms.get(key);
			Lobby lobby = new Lobby(room.getName(), room.getMaxPlayer(), room.getLanguage(),
					room.getRuleSet(), room.isOpen()? Lobby.LobbyStatus.OPEN : Lobby.LobbyStatus.CLOSED);
			lobbies.add(lobby);
		}
		return lobbies;
	}

//	private synchronized boolean roomsFull() {
//		if (actualRooms >= MAX_ROOMS) {
//			return true;
//		}
//		return false;
//	}

}
