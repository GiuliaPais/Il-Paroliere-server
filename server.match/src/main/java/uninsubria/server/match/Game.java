package uninsubria.server.match;

import java.util.ArrayList;
import java.util.List;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.server.roomReference.RoomReference;
import uninsubria.utils.business.Player;

public class Game {

    private RoomReference reference;
    private RoomManager roomManager;
    private ArrayList<ActiveMatch> matches;
    private Player winner;
    private Player[] participants;
    private GameState state;
    private int numMatch;
    private Grid grid;
    private boolean exists;

    public Game(RoomReference r) {
        reference = r;
        roomManager = r.getRoomManager();
        grid = new Grid(roomManager.getLanguage());
        state = GameState.ONGOING;
        numMatch = 0;
        exists = true;
        participants = r.getSlots().toArray(new Player[0]);
    }

    /**
     * Avvia un nuovo match sincronizzando i timer dei player.
     */
    public void newMatch() {
        roomManager.setSyncTimer();
        addMatch();
        playMatch();
        calculateScore();
        endMatch();
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
        roomManager.close();
    }

    /**
     * Restituisce i match fino ad ora avvenuti nella partita.
     * @return i match già avvenuti in partita.
     */
    public List<ActiveMatch> getMatches() {
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

    // Aggiunge un nuovo match al game in corso.
    private void addMatch() {
        numMatch++;
        ActiveMatch match = new ActiveMatch(numMatch, participants, grid, roomManager);
        matches.add(match);
    }

    // Manda ai player, dal match corrente, la grid.
    private void playMatch() {
        String[] grid = matches.get(numMatch).getGrid().toStringArray();
        roomManager.sendGrid(grid);
    }

    // Attende le parole e, una volta ottenute, calcola i punteggi.
    private void calculateScore() {
        roomManager.waitWords();
        matches.get(numMatch).calculateScore();
    }

    // Termina il match calcolando i punteggi e resetta la griglia per il match successivo.
    private void endMatch() {
        roomManager.sendScores(matches.get(numMatch).getScores());
        matches.get(numMatch).Conclude();
    }

}
