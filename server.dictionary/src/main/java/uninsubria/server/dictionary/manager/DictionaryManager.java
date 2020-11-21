/**
 * 
 */
package uninsubria.server.dictionary.manager;

import uninsubria.server.dictionary.core.Dictionary;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.loader.DictionaryLoader;
import uninsubria.utils.dictionary.Definition;
import uninsubria.utils.languages.Language;
import uninsubria.utils.languages.LanguageManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Giulia Pais
 * @version 0.9.1
 */
public class DictionaryManager {
	/*---Fields---*/
	private static DictionaryManager instance;
	private HashMap<Language, Dictionary> dictionaries;

	/*---Constructors---*/
	private DictionaryManager() throws FileNotFoundException, DictionaryException, URISyntaxException, IOException {
		DictionaryLoader dl = new DictionaryLoader();
		this.dictionaries = dl.loadAll();
	}

	/*---Methods---*/
	public static DictionaryManager getInstance() throws IOException, DictionaryException, URISyntaxException {
		if (instance == null) {
			instance = new DictionaryManager();
		}
		return instance;
	}

	public static List<Definition> lookUpWord(String word, Language lang) throws DictionaryException, IOException, URISyntaxException {
		return getInstance().dictionaries.get(lang).getDefinitions(word);
	}
	
	public static boolean isValid(String word, Language lang) throws DictionaryException, IOException, URISyntaxException {
		if (!getInstance().dictionaries.get(lang).wordExists(word)) {
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

	public static List<String> getValidWords(Language lang) throws DictionaryException, IOException, URISyntaxException {
		List<String> validWords = new ArrayList<>();
		for (String w : getInstance().dictionaries.get(lang).getAllWords()) {
			if (isValid(w, lang)) {
				validWords.add(w);
			}
		}
		return validWords;
	}
}
