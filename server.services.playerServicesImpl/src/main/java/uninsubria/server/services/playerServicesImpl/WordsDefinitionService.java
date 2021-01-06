package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.dictionary.Definition;
import uninsubria.utils.languages.Language;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a service that fetches definitions of requested words.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class WordsDefinitionService implements Service {
    /*---Fields---*/
    private Language language;
    private String[] words;

    /*---Constructors---*/
    /**
     * Instantiates a new Words definition service.
     *
     * @param words    the words
     * @param language the language
     */
    public WordsDefinitionService(String[] words, Language language) {
        this.language = language;
        this.words = words;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        HashMap<String, List<Definition>> defs = DictionaryManager.lookUpWord(words, language);
        ServiceResult serviceResult = new ServiceResult("DEFINITION REQUEST");
        for (Map.Entry<String, List<Definition>> entry : defs.entrySet()) {
            Definition[] definitions = entry.getValue().toArray(new Definition[0]);
            Result<Definition[]> result = new Result<>(entry.getKey(), definitions);
            serviceResult.addResult(result);
        }
        return serviceResult;
    }
}
