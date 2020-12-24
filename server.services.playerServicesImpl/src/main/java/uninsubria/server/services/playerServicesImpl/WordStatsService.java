package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.ServiceResultInterface;

/**
 * Implementation of service that requests statistics for a provided word.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class WordStatsService implements Service {
    /*---Fields---*/
    private final String word;

    /*---Constructors---*/
    /**
     * Instantiates a new Word stats service.
     *
     * @param word the word
     */
    public WordStatsService(String word) {
        this.word = word;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager tm = new TransactionManager();
        return tm.requestWordStats(word);
    }
}
