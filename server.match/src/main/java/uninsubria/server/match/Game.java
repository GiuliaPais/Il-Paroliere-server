package uninsubria.server.match;

import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Game {

    private ArrayList<PlayerWrapper> players;
    private Language language;
    private Grid grid;
    private GameState state;
    private int numMatch, maxScoreToWin;
    private ArrayList<ActiveMatch> matches;
    private HashMap<PlayerWrapper, Integer> TotalPlayersScore;
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
        this.matches = new ArrayList<>();
        this.setPlayerScore();
        newMatch();
    }

    /**
     * Inizia un nuovo match del game.
     */
    public void newMatch() {
        numMatch++;
        ActiveMatch match = new ActiveMatch(numMatch, grid, players, language);
        matches.add(match);
        this.getActualMatch().throwDices();
    }

    /**
     * Calcola lo score del match.
     */
    public void calculateMatchScore(HashMap<PlayerWrapper, String[]> map) {
        ArrayList<PlayerScore> list = new ArrayList<>();
        Set<Map.Entry<PlayerWrapper, String[]>> wordsSet = map.entrySet();

        for(Map.Entry<PlayerWrapper, String[]> entry : wordsSet) {
            list.add(new PlayerScore(entry.getKey(), entry.getValue(), language));
        }

        PlayerScore[] playerScores = list.toArray(new PlayerScore[0]);
        this.getActualMatch().calculateScore(playerScores);
    }

    /**
     * Aggiorna i punteggi totali e controlla che ci sia un vincitore.
     */
    public void ConcludeMatchAndCalculateTotalScore() {
        this.getActualMatch().conclude();
        HashMap<PlayerWrapper, Integer> actualMatchScore =  this.getActualMatch().getPlayersScore();

        for(int i = 0; i < actualMatchScore.size(); i++) {
            PlayerWrapper player = players.get(i);

            int matchScore = actualMatchScore.get(player);
            int gameScore = TotalPlayersScore.get(player);
            int sum = matchScore + gameScore;

            TotalPlayersScore.put(player, sum);
        }

        this.checkIfThereIsAWinner();
    }

    /**
     * Restituisce il match attuale.
     * @return il match attuale.
     */
    public ActiveMatch getActualMatch() {
        return matches.get(numMatch-1);
    }

    /**
     * Restituisce l'attuale punteggio di tutti i player.
     * @return un HashMap contenente il Player ed il suo attuale punteggio.
     */
    public HashMap<PlayerWrapper, Integer> getTotalPlayersScore() {
        return TotalPlayersScore;
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
            TotalPlayersScore.remove(player);
    }

    /**
     * Restituisce il vincitore.
     * @return il vincitore come PLayerWrapper.
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
        TotalPlayersScore = new HashMap<>();

        for(int i = 0; i < players.size(); i++) {
            TotalPlayersScore.put(players.get(i), 0);
        }
    }

    // Controlla che ci sia un vincitore e, se c'è ed è unico, termina il game.
    private void checkIfThereIsAWinner() {
        int max = maxScoreToWin;

        for(int i = 0; i < players.size(); i++) {
            PlayerWrapper player = players.get(i);
            int score = TotalPlayersScore.get(player);

            if(score >= max) {
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

    public ArrayList<PlayerWrapper> getPlayers() {
        return players;
    }

}
