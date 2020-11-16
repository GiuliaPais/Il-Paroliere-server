package uninsubria.server.match;

import java.util.List;
import uninsubria.utils.business.Player;

public class Game {

    private List<MatchInterface> matches;
    private Player winner;
    private final List<Player> PARTICIPANTS;
    private GameState state;

    public Game(List<Player> participants) {
        PARTICIPANTS = participants;
        state = GameState.ONGOING;
    }

    /**
     * Aggiunge un nuovo match al game in corso.
     * @param match il nuovo match da aggiungere
     */
    public void add(ActiveMatchInterface match) {

        matches.add(match);
    }

    public void totalScore() {

    }

    /**
     * Rimuove il partecipante passato come argomento e lo espelle dalla stanza.
     * @param player in uscita dal gioco.
     */
    public void abandon(Player player) {
        PARTICIPANTS.remove(player);
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
