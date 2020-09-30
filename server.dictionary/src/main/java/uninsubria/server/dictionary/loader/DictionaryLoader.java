package uninsubria.server.dictionary.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uninsubria.server.dictionary.core.Dictionary;
import uninsubria.utils.dictionary.Definition;
import uninsubria.utils.languages.Language;
import uninsubria.utils.languages.LanguageManager;
/**
 * Provides methods for importing and parsing of the dictionary files for all supported languages.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DictionaryLoader {
	
	/**
	 * Loads and parses all dictionaries for all supported languages.
	 * @return A HashMap where key is a Language object and value is a Dictionary object
	 * @throws FileNotFoundException
	 * @throws DictionaryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public HashMap<Language, Dictionary> loadAll() throws FileNotFoundException, DictionaryException, URISyntaxException, IOException {
		HashMap<Language, Dictionary> dictionaries = new HashMap<>(Language.values().length, 1.0f);
		Dictionary dict;
		for (Language lang : Language.values()) {
			dict = loadFromFile(lang);	
			dictionaries.put(lang, dict);
		}
		return dictionaries;
	}
	
	/**
	 * Loads a single dictionary from file.
	 * @param lang The Language object associated with the dictionary
	 * @return A dictionary object
	 * @throws DictionaryException If errors occurred in retrieving the dictionary file
	 * @throws URISyntaxException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Dictionary loadFromFile(Language lang) throws DictionaryException, URISyntaxException, FileNotFoundException, IOException {
		LanguageManager lm = new LanguageManager(lang);
		File dict = lm.getDictionaryFile();
		if (dict == null) {
			throw new DictionaryException("Unable to load dictionary file");
		}
		Dictionary d = null;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dict), lm.getDictEncoding()))) {
			String curr = null;
			d = new Dictionary();
			curr = in.readLine();
			while(curr != null) {
				parseTerm(curr, in, d, lm);
				curr = in.readLine();
			}
			return d;
		}
	}
	
	/**
	 * Method for parsing a single term and adding it to the dictionary.
	 * @param term The term to parse
	 * @param in The BufferedReader object in use
	 * @param dict The dictionary to modify
	 * @param lm The LanguageManager object in use
	 * @throws IOException
	 */
	private void parseTerm(String term, BufferedReader in, Dictionary dict, LanguageManager lm) throws IOException {
		if (!term.contains("|")) {
			return;
		}
		String[] split = term.split("\\|");
		String key = split[0];
		int numdefs = Integer.parseInt(split[1]);
		List<Definition> defs = new ArrayList<>(numdefs);
		String cur;
		for (int i = 0; i < numdefs; i++) {
			cur = in.readLine();
			defs.add(parseDef(cur, lm));
		}
		dict.addEntry(key, defs);
	}
	
	/**
	 * Parses a string to obtain a Definition object.
	 * @param def The string to parse
	 * @param lm The LanguageManager object in use
	 * @return a Definition object
	 */
	private Definition parseDef(String def, LanguageManager lm) {
		String[] tokens = def.split("\\|");
		String firstdef = tokens[0];
		String[] firstDefTokens = firstdef.split("\\s");
		String typeToken = firstDefTokens[0];
		typeToken = typeToken.replaceAll("\\(", "");
		typeToken = typeToken.replaceAll("\\)", "");
		String defType;
		HashMap<String, String> abbrev = lm.getAbbreviations();
		if (abbrev.containsKey(typeToken)) {
			defType = abbrev.get(typeToken);
		} else {
			defType = lm.getAlternativeTermType();
		}
		String defString = "";
		if (firstDefTokens.length == 2) {
			defString = firstDefTokens[1].replaceAll("\\)", "");
		}
		Definition definition = new Definition(defType, defString);
		for (int i = 1; i < tokens.length; i++) {
			definition.addSynonym(tokens[i]);
		}
		return definition;
	}
}
