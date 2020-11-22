package uninsubria.server.match;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.server.roomReference.RoomReference;
import uninsubria.utils.business.Player;

public class Game {

    private RoomReference reference;
    private RoomManager roomManager;
    private ArrayList<MatchInterface> matches;
    private Player winner;
    private Player[] participants;
    private GameState state;
    private int numMatch;
    private boolean exists;

    public Game(RoomReference r) {
        reference = r;
        roomManager = r.getRoomManager();
        state = GameState.ONGOING;
        numMatch = 0;
        exists = true;

        setParticipants(r.getSlots());
    }

    public void newMatch() {
        try {
            roomManager.setSyncTimer();
            roomManager.synchronizeClocks(0,5,0);
        } catch (IOException ignored) { }
        addMatch();
    }

    /**
     * Metodo per i test. Restituisce true se la classe è stata istanziata.
     * @return true se istanziato.
     */
    public boolean exists() {
        return exists;
    }

    public void totalScore() {

    }

    /**
     * Rimuove il partecipante passato come argomento e lo espelle dalla stanza.
     * @param player in uscita dal gioco.
     */
    public void abandon(Player player) {
        reference.leaveRoom(player);
        state = GameState.INTERRUPTED;
        roomManager.getChronometer().interrupt();
    }

    /**
     * Restituisce i match fino ad ora avvenuti nella partita.
     * @return i match già avvenuti in partita.
     */
    public List<MatchInterface> getMatches() {
        return matches;
    }

    /**
     * Restituisce il nome del vincitore.
     * @return il nome del vincitore come Stringa.
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Setta un nuovo vincitore
     * @param winner il nuovo vincitore.
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Restituisce la lista dei partecipanti attuali.
     * @return i partecipanti attuali.
     */
    public Player[] getParticipants() {
        return participants;
    }

    /**
     * Ottiene l'attuale stato di gioco.
     * @return l'attuale stato di gioco.
     */
    public GameState getGameState() {
        return state;
    }

    // Trasforma l'ArrayList di player passato come argomento nell'array di Player.
    private void setParticipants(ArrayList<Player> p) {
        participants = new Player[p.size()];

        for(int i = 0; i < p.size(); i++) {
            participants[i] = p.get(i);
        }
    }

    // Aggiunge un nuovo match al game in corso e manda la griglia corrispondente ai player.
    private void addMatch() {
        numMatch++;
        ActiveMatch match = new ActiveMatch(numMatch, participants);
        matches.add(match);

        try {
            String strGrid = match.getGrid().toString();
            roomManager.sendGrid(strGrid);
            roomManager.synchronizeClocks(3,0,0);
 //           roomManager.sendScores();
        } catch (IOException e) { }
    }

}
