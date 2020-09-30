/**
 * 
 */
package uninsubria.server.dictionary.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import uninsubria.server.dictionary.core.Dictionary;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.loader.DictionaryLoader;
import uninsubria.utils.dictionary.Definition;
import uninsubria.utils.languages.Language;
import uninsubria.utils.languages.LanguageManager;

/**
 * @author Giulia Pais
 *
 */
public class DictionaryManager {
	/*---Fields---*/
	private HashMap<Language, Dictionary> dictionaries;

	/*---Constructors---*/
	public DictionaryManager() throws FileNotFoundException, DictionaryException, URISyntaxException, IOException {
		DictionaryLoader dl = new DictionaryLoader();
		this.dictionaries = dl.loadAll();
	}

	/*---Methods---*/
	public List<Definition> lookUpWord(String word, Language lang) {
		return dictionaries.get(lang).getDefinitions(word);
	}
	
	public boolean isValid(String word, Language lang) {
		if (!dictionaries.get(lang).wordExists(word)) {
			return false;
		}
		List<Definition> defs = lookUpWord(word, lang);
		LanguageManager lm = new LanguageManager(lang);
		List<String> validTypes = lm.getValidTypes();
		for (Definition def : defs) {
			if (validTypes.contains(def.getType())) {
				return true;
			}
		}
		return false;
	}
}
