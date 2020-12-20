package uninsubria.server.match;

import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private ArrayList<PlayerWrapper> players;
    private Language language;
    private Grid grid;
    private GameState state;
    private int numMatch, maxScoreToWin;
    private ArrayList<ActiveMatch> matches;
    private HashMap<PlayerWrapper, Integer> playersScore;
    private PlayerWrapper winner;
    private boolean interruptIfSomeoneLeaves, thereIsAWinner;

    public Game(ArrayList<PlayerWrapper> players, Language language, Ruleset ruleset) {
        this.players = players;
        this.language = language;

        interruptIfSomeoneLeaves = ruleset.interruptIfSomeoneLeaves();
        maxScoreToWin = ruleset.getMaxScoreToWin();
        grid = new Grid(language);
        state = GameState.ONGOING;
        numMatch = 0;
        thereIsAWinner = false;

        this.setPlayerScore();
    }

    /**
     * Inizia un nuovo match del game.
     */
    public void newMatch() {
        numMatch++;
        ActiveMatch match = new ActiveMatch(numMatch, grid, players, language);
        matches.add(match);

        ActiveMatch actualMatch = matches.get(numMatch);
        actualMatch.throwDices();
    }

    /**
     * Calcola lo score del match.
     */
    public void calculateMatchScore() {
        this.getActualMatch().calculateScore();
    }

    /**
     * Aggiorna i punteggi totali e controlla che ci sia un vincitore.
     */
    public void calculateTotalScore() {
        HashMap<PlayerWrapper, Integer> actualMatchScore =  this.getActualMatch().getPlayersScore();

        for(int i = 0; i < actualMatchScore.size(); i++) {
            PlayerWrapper player = players.get(i);

            int matchScore = actualMatchScore.get(player);
            int gameScore = playersScore.get(player);
            int sum = matchScore + gameScore;

            playersScore.put(player, sum);
        }

        this.checkIfThereIsAWinner();
    }

    /**
     * Restituisce il match attuale.
     * @return il match attuale.
     */
    public ActiveMatch getActualMatch() {
        return matches.get(numMatch);
    }

    /**
     * Restituisce l'attuale punteggio di tutti i player.
     * @return un HashMap contenente il Player ed il suo attuale punteggio.
     */
    public HashMap<PlayerWrapper, Integer> getPlayersScore() {
        return playersScore;
    }

    /**
     * Rimuove il partecipante dal gioco.
     * @param player da rimuovere.
     */
    public void abandon(PlayerWrapper player) {
        players.remove(player);

        if(interruptIfSomeoneLeaves)
            state = GameState.INTERRUPTED;

        else
            playersScore.remove(player);
    }

    /**
     * Restituisce il nome del vincitore.
     * @return il nome del vincitore come Stringa.
     */
    public PlayerWrapper getWinner() {
        return winner;
    }

    public boolean thereIsAWinner() {
        return thereIsAWinner;
    }

    /**
     * Ottiene l'attuale stato di gioco.
     * @return l'attuale stato di gioco.
     */
    public GameState getGameState() {
        return state;
    }

    // Setta l'attuale punteggio di tutti i player a 0.
    private void setPlayerScore() {
        playersScore = new HashMap<>();

        for(int i = 0; i < players.size(); i++) {
            playersScore.put(players.get(i), 0);
        }
    }

    // Controlla che ci sia un vincitore e, se c'è ed è unico, termina il game.
    private void checkIfThereIsAWinner() {
        int max = maxScoreToWin;

        for(int i = 0; i < players.size(); i++) {
            PlayerWrapper player = players.get(i);
            int score = playersScore.get(player);

            if(score > max) {
                max = score;
                winner = player;
                thereIsAWinner = true;

            } else if(score == max) {
                thereIsAWinner = false;
            }
        }

        if(thereIsAWinner) {
           state = GameState.FINISHED;
        }
    }

}
