package uninsubria.server.scoreCounter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uninsubria.utils.languages.Language;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateWordsTest {

    private DuplicateWords test;
    private Language language = Language.ITALIAN;

    @BeforeEach
    void setUp() {
        test  = new DuplicateWords(language);
    }

    @Test
    void addWords() {
        String[] array = {"Uno", "Due", "Tre", "Tre"};
        String[] array2 = {"Due"};
        test.addWords(array);
        test.addWords(array2);

        assertEquals(3, test.getWords().size());
        assertEquals(true, test.getWords().get(1).isDuplicated());
        assertEquals(false, test.getWords().get(0).isDuplicated());
    }

    @Test
    void getDuplicatedWords() {
        String[] array = {"Uno", "Due", "Tre", "Tre"};
        String[] array2 = {"Due"};
        test.addWords(array);
        test.addWords(array2);

        assertEquals(2, test.getDuplicatedWords().size());
        assertEquals("Due", test.getDuplicatedWords().get(0).getWord());
        assertEquals("Tre", test.getDuplicatedWords().get(1).getWord());
    }
}