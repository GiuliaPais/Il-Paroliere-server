/**
 * 
 */
package uninsubria.server.dictionary.manager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.utils.dictionary.Definition;
import uninsubria.utils.languages.Language;

/**
 * @author Giulia Pais
 *
 */
class DictionaryManagerTest {
	
	static DictionaryManager dm;
	
	@BeforeAll
	public static void init() {
		try {
			dm = dm.getInstance();
		} catch (DictionaryException | URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testLookUpWord() throws IOException, DictionaryException, URISyntaxException {
		List<Definition> defs_ok = DictionaryManager.lookUpWord("casa", Language.ITALIAN);
		assertNotNull(defs_ok);
		List<Definition> defs_ko = DictionaryManager.lookUpWord("ergtet", Language.ITALIAN);
		assertNull(defs_ko);
		List<Definition> defs_ok_eng = DictionaryManager.lookUpWord("home", Language.ENGLISH);
		assertNotNull(defs_ok_eng);
		List<Definition> defs_ko_eng = DictionaryManager.lookUpWord("ergtet", Language.ENGLISH);
		assertNull(defs_ko_eng);
	}
	
	@Test
	void testIsValid() throws IOException, DictionaryException, URISyntaxException {
		boolean valid_ok = DictionaryManager.isValid("casa", Language.ITALIAN);
		assertTrue(valid_ok);
		boolean valid_ok_title = DictionaryManager.isValid("Casa", Language.ITALIAN);
		assertTrue(valid_ok_title);
		boolean valid_ok_upper = DictionaryManager.isValid("CASA", Language.ITALIAN);
		assertTrue(valid_ok_upper);
		boolean valid_ko = DictionaryManager.isValid("per", Language.ITALIAN);
		assertFalse(valid_ko);
		boolean valid_ok_eng = DictionaryManager.isValid("home", Language.ENGLISH);
		assertTrue(valid_ok_eng);
		boolean valid_ko_eng = DictionaryManager.isValid("the", Language.ENGLISH);
		assertFalse(valid_ko_eng);
		boolean valid_ko_missing = DictionaryManager.isValid("fdgfdg", Language.ITALIAN);
		boolean valid_ko_missing_eng = DictionaryManager.isValid("fdgfdg", Language.ENGLISH);
		assertFalse(valid_ko_missing);
		assertFalse(valid_ko_missing_eng);
	}
}
