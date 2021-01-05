package uninsubria.server.services.roomServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.server.wrappers.GameEntriesWrapper;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.UUID;

/**
 * Implementation of a service that registers game statistics in the database.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class RegisterGameStatsService implements Service {
    /*---Fields---*/
    /* For game info tuple */
    private final UUID gameID;
    private final String[] gameGrids;
    private final Integer numPlayers;
    private final Ruleset ruleset;
    private final Language language;

    /* For game entries */
    private final GameEntriesWrapper entriesInfo;

    /*---Constructors---*/
    public RegisterGameStatsService(UUID gameID, String[] gameGrids, Integer numPlayers, Ruleset ruleset, Language language, GameEntriesWrapper entriesInfo) {
        this.gameID = gameID;
        this.gameGrids = gameGrids;
        this.numPlayers = numPlayers;
        this.ruleset = ruleset;
        this.language = language;
        this.entriesInfo = entriesInfo;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.registerGameStats(gameID, gameGrids, numPlayers, ruleset, language, entriesInfo);
        return null;
    }
}
