package uninsubria.server.room;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import uninsubria.utils.business.Player;

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

	private static final int MAX_ROOMS = 10;
	private static RoomList instance;

	private MapProperty<Integer, Room> rooms;
	private int idRoom, actualRooms;

	/**
	 * Istanza privata, necessario usare il metodo getInstance().
	 */
	private RoomList() {
		idRoom = 0;
		actualRooms = 0;
		rooms = new SimpleMapProperty<Integer, Room>(FXCollections.observableMap(new ConcurrentHashMap<>()));
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
	public static Map<Integer, Room> getRoomList() {
		return getInstance().rooms.get();
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
		//Questo metodo va sistemato perchè va chiamato da un servizio con i parametri appropriati,
		//servono ruleset (appena implementato blandamente) e classe adapter per player contenente l'ip
		if (roomsFull()) {
			//send errors
			return;
		}
		getInstance().incrementRoom();
		Room tmp = new Room(idRoom, player); //Anche i costruttori della stanza vanno modificati per includere lingua, set di regole e adapter
		getInstance().rooms.put(idRoom, tmp);
	}

	/**
	 * Permette al player passato come secondo parametro di unirsi alla stanza il cui id è uguale a quello passato
	 * come primo parametro.
	 * @param id
	 * @param player
	 */
	public synchronized void joinRoom(int id, Player player) {
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

	private synchronized boolean roomsFull() {
		if (actualRooms >= MAX_ROOMS) {
			return true;
		}
		return false;
	}

	public void addListener(MapChangeListener<? super Integer, ? super Room> listener) {
		getInstance().rooms.addListener(listener);
	}
}
