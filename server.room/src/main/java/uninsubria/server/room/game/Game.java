package uninsubria.server.room.game;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uninsubria.server.room.RoomLeaveMonitor;
import uninsubria.server.room.match.Match;
import uninsubria.server.roomManager.RoomManager;
import uninsubria.server.roomlist.RoomList;
import uninsubria.server.scoreCounter.DuplicateWords;
import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.server.scoreCounter.WordAnalyzer;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.Word;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing an active game.
 *
 * @author Giulia Pais
 * @author Davide Di Giovanni
 * @version 0.9.7
 */
public class Game implements Runnable {
    /*---Fields---*/
    private final ArrayList<PlayerWrapper> players;
    private final Ruleset ruleset;
    private final RoomManager roomManager;
    private final Language language;
    private final UUID roomID;
    private final ObjectProperty<GameState> gameStatus;
    private final RoomLeaveMonitor monitor;

    //++ Matches ++//
    private final Grid currentMatchGrid;
    private int lastMatchIndex;
    private final ArrayList<Match> matches;

    //++ Game ++//
    private List<String> totalGameGrid;
    private HashMap<String, Integer> totalGameScores;
    private String winner;

    /*---Constructors---*/
    public Game(ArrayList<PlayerWrapper> players, Ruleset ruleset, Language language, UUID roomId, RoomLeaveMonitor monitor) {
        this.players = players;
        this.ruleset = ruleset;
        this.language = language;
        this.roomID = roomId;
        this.currentMatchGrid = new Grid(language);
        currentMatchGrid.throwDices();
        this.roomManager = new RoomManager();
        this.matches = new ArrayList<>();
        this.totalGameGrid = new ArrayList<>(Arrays.asList(currentMatchGrid.getDiceFaces()));
        this.lastMatchIndex = 0;
        this.gameStatus = new SimpleObjectProperty<>(GameState.STARTING);
        this.totalGameScores = new HashMap<>();
        this.monitor = monitor;
    }

    /*---Methods---*/
    @Override
    public void run() {
        for (PlayerWrapper player : players) {
            /* Try establishing a connection with every player. If a connection cannot be established,
            * the player is kicked from the game and the room, then the ruleset is evaluated to decide
            * if the game should be interrupted or not */
            try {
                roomManager.addRoomProxy(player);
            } catch (IOException e) {
                players.remove(player);
                RoomList.getRoom(roomID).leaveRoom(player.getPlayerID());
                if (ruleset.interruptIfSomeoneLeaves()) {
                    setGameStatus(GameState.INTERRUPTED);
                    roomManager.interruptGame();
                    roomManager.terminateManager();
                    return;
                }
            }
        }
        /* Contact new players announcing a new game*/
        List<Instant> timerInstant = roomManager.newGame();
        if (timerInstant.size() < players.size()) { //means one or more players weren't reachable
            for (PlayerWrapper p : players) {
                if (!roomManager.getPlayers().contains(p)) {
                    players.remove(p);
                    RoomList.getRoom(roomID).leaveRoom(p.getPlayerID());
                    if (ruleset.interruptIfSomeoneLeaves()) {
                        setGameStatus(GameState.INTERRUPTED);
                        roomManager.interruptGame();
                        roomManager.terminateManager();
                        return;
                    }
                }
            }
        }
        Instant max = timerInstant.stream().max(Instant::compareTo).get().plusSeconds(ruleset.getTimeToMatch().toSeconds());
        /* Sleep until the first match is finished on client side. The sleep time (for standard ruleset) is calculated as:
         * - select the later instant obtained by pinging the clients
         * - Add 3 minutes (the duration of a match) */
        long sleepTime = Instant.now().until(max, ChronoUnit.MILLIS);
        setGameStatus(GameState.ONGOING);
        do {
            //At least one match
            if (getGameStatus().equals(GameState.INTERRUPTED)) {
                return;
            }
            newMatch(sleepTime);
            List<String> gridAsList = Arrays.asList(currentMatchGrid.getDiceFaces());
            //Update the total game grid
            totalGameGrid = Stream.concat(totalGameGrid.stream(), gridAsList.stream()).collect(Collectors.toList());
            //If there's another match reset the grid
            if (winner == null) {
                currentMatchGrid.resetDices();
                currentMatchGrid.throwDices();
                sleepTime = ruleset.getTimeToMatch().plusSeconds(5).toMillis();
            }
        } while (winner == null);
        roomManager.endGame();
        try {
            Thread.sleep(Duration.ofSeconds(35).toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setGameStatus(GameState.FINISHED);
    }

    public GameState getGameStatus() {
        return gameStatus.get();
    }

    public ObjectProperty<GameState> gameStatusProperty() {
        return gameStatus;
    }

    public void setGameStatus(GameState gameStatus) {
        this.gameStatus.set(gameStatus);
    }

    private void newMatch(long sleepTime) {
        /* Contact new players announcing a new match and sending the grid */
        roomManager.newMatch(currentMatchGrid.getDiceFaces(), currentMatchGrid.getDiceNumb());
        if (sleepTime > 0) {
            try {
                boolean to = monitor.isSomeoneLeaving(sleepTime);
                if (to & ruleset.interruptIfSomeoneLeaves()) {
                    throw new InterruptedException();
                }
            } catch (InterruptedException e) {
                setGameStatus(GameState.INTERRUPTED);
                List<PlayerWrapper> leavingPlayers = monitor.getPlayersLeaving();
                players.removeAll(leavingPlayers);
                roomManager.interruptGame();
                return;
            }
        }
        /* Requests the words list from players */
        HashMap<PlayerWrapper, String[]> playersWords = roomManager.readWords();
        GameScore scores = calculateScores(playersWords);
        roomManager.sendScores(scores);
        Match match = new Match(++lastMatchIndex, scores.getMatchWords());
        matches.add(match);
        roomManager.setMatchTimeout();
    }

    private GameScore calculateScores(HashMap<PlayerWrapper, String[]> playersWords) {
        ArrayList<PlayerScore> list = new ArrayList<>();
        for(Map.Entry<PlayerWrapper, String[]> entry : playersWords.entrySet()) {
            list.add(new PlayerScore(entry.getKey().getPlayerID(), entry.getValue(), language));
        }
        PlayerScore[] playerScores = list.toArray(new PlayerScore[0]);
        DuplicateWords checkDuplicated = new DuplicateWords(language);
        for(PlayerScore ps : playerScores)
            checkDuplicated.addWords(ps.getWords());
        for(PlayerScore ps : playerScores)
            ps.setDuplicateWords(checkDuplicated.getDuplicatedWords());
        GameScore gameScore = new GameScore();
        for (PlayerScore ps : playerScores) {
            WordAnalyzer[] analyzers = ps.getWords();
            Word[] wordsFound = new Word[analyzers.length];
            for (int i = 0; i < wordsFound.length; i++) {
                Word w = new Word();
                w.setWord(analyzers[i].getWord());
                w.setDuplicated(analyzers[i].isDuplicated());
                w.setWrong(!analyzers[i].isValid());
                w.setPoints(analyzers[i].getScore());
                wordsFound[i] = w;
            }
            totalGameScores.merge(ps.getPlayer(), ps.getTotalScore(), Integer::sum);
            gameScore.setScoresForPlayer(ps.getPlayer(), ps.getTotalScore(), totalGameScores.get(ps.getPlayer()), wordsFound);
        }
        winner = checkWinner();
        if (winner != null) {
            gameScore.setWinner(winner);
        }
        return gameScore;
    }

    /* Checks if any of the players has reached the minimum score to win. If there is a maximum AND IT'S UNIQUE,
    * it returns the name of the player, otherwise returns null (even in a tie situation) */
    private String checkWinner() {
        Map.Entry<Integer, Long> max = totalGameScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= ruleset.getMaxScoreToWin())
                .collect(Collectors.groupingBy(e -> e.getValue(), TreeMap::new, Collectors.counting()))
                .lastEntry();
        if (max == null || max.getValue() > 1) {
            return null;
        }
        String winn = totalGameScores.entrySet().stream()
                .filter(e -> e.getValue().equals(max.getKey()))
                .map(e -> e.getKey())
                .collect(Collectors.joining());
        return winn;
    }
}
