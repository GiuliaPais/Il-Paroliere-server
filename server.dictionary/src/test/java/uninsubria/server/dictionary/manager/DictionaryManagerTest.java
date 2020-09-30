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
			dm = new DictionaryManager();
		} catch (DictionaryException | URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testLookUpWord() {
		List<Definition> defs_ok = dm.lookUpWord("casa", Language.ITALIAN);
		assertNotNull(defs_ok);
		List<Definition> defs_ko = dm.lookUpWord("ergtet", Language.ITALIAN);
		assertNull(defs_ko);
		List<Definition> defs_ok_eng = dm.lookUpWord("home", Language.ENGLISH);
		assertNotNull(defs_ok_eng);
		List<Definition> defs_ko_eng = dm.lookUpWord("ergtet", Language.ENGLISH);
		assertNull(defs_ko_eng);
	}
	
	@Test
	void testIsValid() {
		boolean valid_ok = dm.isValid("casa", Language.ITALIAN);
		assertTrue(valid_ok);
		boolean valid_ko = dm.isValid("per", Language.ITALIAN);
		assertFalse(valid_ko);
		boolean valid_ok_eng = dm.isValid("home", Language.ENGLISH);
		assertTrue(valid_ok_eng);
		boolean valid_ko_eng = dm.isValid("the", Language.ENGLISH);
		assertFalse(valid_ko_eng);
		boolean valid_ko_missing = dm.isValid("fdgfdg", Language.ITALIAN);
		boolean valid_ko_missing_eng = dm.isValid("fdgfdg", Language.ENGLISH);
		assertFalse(valid_ko_missing);
		assertFalse(valid_ko_missing_eng);
	}
}
