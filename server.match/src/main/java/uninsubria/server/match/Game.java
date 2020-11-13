package uninsubria.server.match;

import java.util.List;

import tmpClasses.Player;

public class Game {

    private List<MatchInterface> matches;
    private Player winner;
    private List<Player> participants;
    private GameState state;

    public Game(List<Player> participants) {
        this.participants = participants;
        state = GameState.ONGOING;
    }

    /**
     * Aggiunge un nuovo match al game in corso.
     * @param match
     */
    public void add(ActiveMatchInterface match) {
        matches.add(match);
    }

    public void totalScore() {

    }

    /**
     * Rimuove il partecipante passato come argomento.
     * @param player
     */
    public void abandon(Player player) {
        participants.remove(player);
    }

    /**
     * Restituisce i match fino ad ora avvenuti nella partita.
     * @return i match già avvenuti in partita.
     */
    public List<MatchInterface> getMatches() {
        return matches;
    }

    /**
     * Setta i nuovi match.
     * @param matches
     */
    public void setMatches(List<MatchInterface> matches) {
        this.matches = matches;
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
     * @param winner
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Restituisce la lista dei partecipanti attuali.
     * @return i partecipanti attuali.
     */
    public List<Player> getParticipants() {
        return participants;
    }

    /**
     * Setta i nuovi partecipanti alla partita.
     * @param participants
     */
    public void setParticipants(List<Player> participants) {
        this.participants = participants;
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
     * @param state
     */
    public void setState(GameState state) {
        this.state = state;
    }
}
