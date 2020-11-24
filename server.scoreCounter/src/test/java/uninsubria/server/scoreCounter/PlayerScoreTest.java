package uninsubria.server.scoreCounter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerScoreTest {

    private PlayerScore test;
    private Player player;
    private Language language;

    @BeforeEach
    void setUp() {
        player = new Player();
        language = Language.ITALIAN;
        String[] words = {"CASA", "ABBAIARE", "HO"};
        test = new PlayerScore(player, words, language);
    }

    @Test
    void setDuplicateWords() {
        WordAnalyzer w1 = new WordAnalyzer("Abbaiare", language);
        ArrayList<WordAnalyzer> list = new ArrayList<>();
        list.add(w1);

        test.setDuplicateWords(list);

        assertEquals(true, test.getWords()[1].isDuplicated());
        assertEquals(1, test.getTotalScore());
    }

    @Test
    void getTotalScore() {
        // Casa = 1, Abbaiare = 11, Ho = 0
        assertEquals(12, test.getTotalScore());
    }
}