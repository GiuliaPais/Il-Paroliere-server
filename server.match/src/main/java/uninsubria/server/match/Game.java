package uninsubria.server.match;

import java.io.IOException;
import java.util.List;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.server.roomReference.RoomReference;
import uninsubria.utils.business.Player;

public class Game {

    private RoomReference reference;
    private RoomManager roomManager;
    private List<MatchInterface> matches;
    private Player winner;
    private final List<Player> PARTICIPANTS;
    private GameState state;
    private int numMatch;

    public Game(RoomReference r) {
        reference = r;
        roomManager = r.getRoomManager();
        PARTICIPANTS = reference.getSlots();
        state = GameState.ONGOING;
        numMatch = 0;
    }

    /**
     * Aggiunge un nuovo match al game in corso e manda la griglia corrispondente ai player.
     */
    public void newMatch() {
        numMatch++;
        Player[] p = (Player[])PARTICIPANTS.toArray();

        ActiveMatch match = new ActiveMatch(numMatch, p);
        matches.add(match);

        try {
            String strGrid = match.getGrid().toString();
            roomManager.sendGrid(strGrid);
        } catch (IOException e) { }
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
    public List<Player> getParticipants() {
        return PARTICIPANTS;
    }

    /**
     * Ottiene l'attuale stato di gioco.
     * @return l'attuale stato di gioco.
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * Setta un nuovo stato di gioco.
     * @param state lo stato di gioco.
     */
    public void setState(GameState state) {
        this.state = state;
    }
}
