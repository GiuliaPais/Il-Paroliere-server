package uninsubria.server.scoreCounter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.utils.languages.Language;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class WordAnalyzerTest {

    private WordAnalyzer test;

    @BeforeEach
    void setUp() {
        test = new WordAnalyzer("CASA", Language.ITALIAN);
    }

    @Test
    void isValid() {
        assertEquals(true, test.isValid());
    }

    @Test
    void isDuplicated() {
        assertEquals(false, test.isDuplicated());
    }

    @Test
    void getScore() {
        assertEquals(1, test.getScore());

        test.setWord("ELPOLLOLOCO");
        assertEquals(0, test.getScore());

        test.setWord("Abbaiare");
        assertEquals(11, test.getScore());

        test.setWord("PR");
        assertEquals(0, test.getScore());
    }

    @Test
    void testEquals() {
        WordAnalyzer test2 = new WordAnalyzer("CASA", Language.ENGLISH);
        assertEquals(true, test.equals(test2));

        assertEquals(true, test.equals("caSa"));
    }

    @Test
    void testToString() {
        String result = "Casa true false 1";
        assertEquals(result, test.toString());
    }

    @Test
    void setDuplicate() {
        test.setDuplicate(true);
        assertEquals(0, test.getScore());

        test.setDuplicate(false);
        assertEquals(1, test.getScore());
    }
}