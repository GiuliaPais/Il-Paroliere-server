package uninsubria.server.dictionary.core;

import java.util.HashMap;
import java.util.List;
import uninsubria.utils.dictionary.Definition;

/**
 * Class representing a dictionary. A dictionary has an internal HashMap where
 * terms are keys and each term is associated with a list of definitions.
 * 
 * @author Giulia Pais
 * @version 0.9.0
 */
public class Dictionary {
	/*---Fields---*/
	/**
	 * The actual dictionary
	 */
	private HashMap<String, List<Definition>> dict;
	
	/*---Constructors---*/
	/**
	 * Returns an object of class Dictionary
	 */
	public Dictionary() {
		this.dict = new HashMap<>();
	}

	/*---Methods---*/
	/**
	 * Adds an entry to the dictionary.
	 * @param key The term to use as key
	 * @param defs The list of Definition objects
	 */
	public void addEntry(String key, List<Definition> defs) {
		dict.put(key, defs);
	}
	
	/**
	 * Checks if a word exists in the given dictionary.
	 * @param word The term to look for
	 * @return TRUE or FALSE
	 */
	public boolean wordExists(String word) {
		return dict.containsKey(word);
	}
	
	/**
	 * Returns the list of definitions for a given term.
	 * @param word The term to look for
	 * @return A list of Definition objects
	 */
	public List<Definition> getDefinitions(String word) {
		return dict.get(word);
	}
	
}
