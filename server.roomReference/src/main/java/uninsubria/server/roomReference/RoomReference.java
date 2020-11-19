package uninsubria.server.roomReference;

import uninsubria.utils.business.Player;

import java.util.ArrayList;

public class RoomReference {

    private RoomManager manager;
    private RoomState state;
    private int maxPlayer;
    private int actualPlayer;

    private ArrayList<Player> slots;

    /**
     * La stanza viene creata nel momento in cui un player vi entra. Fintanto che la stanza esiste,
     * sarà possibile per altri aggiugnervisi fintanto che non sarà raggiunto il numero massimo di player consentito.
     * @param player il primo player ad entrare nella stanza, colui che ne richiede la creazione
     */
    public RoomReference(Player player) {
        state = RoomState.OPEN;
        slots = new ArrayList<>();
        maxPlayer = 6;
        actualPlayer = 0;

        this.joinRoom(player);
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
     * I valori ammessi sono compresi tra 2 e 6. Valori minori o maggiori impostano rispettivamente
     * al minimo ed al massimo consentito.
     * @param i il nuovo numero massimo di giocatori consentito.
     */
    public void setMaxPlayer(int i) {
        if(actualPlayer <= i) {
            if (i < 2)
                maxPlayer = 2;
            else if (i > 6)
                maxPlayer = 6;
            else
                maxPlayer = i;
        }
    }

    public void setActualPlayer(int i) {
        actualPlayer = i;
    }

    /**
     * Restituisce il numero attuale di player nella stanza
     * @return il numero di player nella stanza
     */
    public int getActualPlayer() {
        return actualPlayer;
    }

    /**
     Restituisce il numero massimo di player che la stanza può accettare.
     * @return il numero di player massimo della stanza.
     */
    public int getMaxPlayer() {
        return maxPlayer;
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
     * Restituisce lo stato attuale della stanza.
     * @return RoomState, lo stato attuale della stanza.
     */
    public RoomState getRoomState() {
        return state;
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
    public boolean newGameIsPossible() {
        if(actualPlayer == maxPlayer) {
            manager = new RoomManager(slots);
            state = RoomState.GAMEON;
            return true;

        } else
            return false;
    }

    /**
     * Restituisce l'attuale RoomManager se istanziato. Null altrimenti.
     * @return RoomManager.
     */
    public RoomManager getRoomManager() {
        return manager;
    }

}

