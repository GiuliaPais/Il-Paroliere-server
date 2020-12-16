package uninsubria.server.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.server.roomReference.RoomReference;
import uninsubria.utils.business.Player;
import uninsubria.utils.chronometer.*;

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
    private HashMap<Player, Integer> playersScore;

    public Game() {

    }

    public Game(RoomReference r) {
        reference = r;
        roomManager = r.getRoomManager();
        grid = new Grid(roomManager.getLanguage());
        state = GameState.ONGOING;
        numMatch = 0;
        exists = true;
        participants = r.getSlots().toArray(new Player[0]);

        setPlayerScore();
    }

    /**
     * Avvia un nuovo match sincronizzando i timer dei player.
     */
    public void newMatch() {
        if(!state.equals(GameState.FINISHED)) {

            roomManager.setSyncTimer(5000L);
            addMatch();

            ActiveMatch matchTmp = matches.get(numMatch);
            matchTmp.throwDices();

            waitTheEnd(3, 0, 0);

            matchTmp.calculateScore();
            matchTmp.conclude();

            controlScore(matchTmp);
        }
    }

    /**
     * Metodo per i test. Restituisce true se la classe è stata istanziata.
     * @return true se istanziato.
     */
    public boolean exists() {
        return exists;
    }

    /**
     * Restituisce l'attuale punteggio di tutti i player.
     * @return un HashMap contenente il Player ed il suo attuale punteggio.
     */
    public HashMap<Player, Integer> getPlayersScore() {
        return playersScore;
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

    // Tempo di attesa per la fine del turno o di un caricamento.
    private void waitTheEnd(int min, int sec, int millis) {
        Counter counter = new Counter(min, sec, millis);
        Chronometer chronometer = new Chronometer(counter);
        try {
            chronometer.join();
        } catch (InterruptedException ignored) { }
    }

    // Setta l'attuale punteggio di tutti i player a 0.
    private void setPlayerScore() {
        playersScore = new HashMap<>();

        for(int i = 0; i < participants.length; i++) {
            playersScore.put(participants[i], 0);
        }
    }

    // Somma i valori delle due hashMap e li aggionra in playersScore.
    private void addScore(HashMap<Player, Integer> matchScores) {
        for(int i = 0; i < matchScores.size(); i++) {
            Player player = participants[i];
            int matchScore = matchScores.get(player);
            int gameScore = playersScore.get(player);
            int sum = matchScore + gameScore;
            playersScore.put(player, sum);
        }
    }

    // Controlla i punteggi e se ci sia o meno un vincitore.
    private void controlScore(ActiveMatch match) {
        addScore(match.getPlayersScore());
        controlWinner();

    }

    // Controlla che ci sia un vincitore e, se c'è ed è unico, termina il game.
    private void controlWinner() {
        boolean moreThanOne = false;
        int max = 0;

        for(int i = 0; i < participants.length; i++) {
            Player player = participants[i];
            int score = playersScore.get(player);

            if(score > max) {
                max = score;
                winner = player;
            } else if(score == max) {
                moreThanOne = true;
            }
        }

        if(max >= 50 && !moreThanOne) {
           state = GameState.FINISHED;
           roomManager.endGame(winner.getName(), max);
           roomManager.close();
        }

    }

}
