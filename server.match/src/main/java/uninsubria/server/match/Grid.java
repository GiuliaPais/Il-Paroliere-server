package uninsubria.server.match;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import uninsubria.server.dice.DiceSet;
import uninsubria.server.dice.DiceSetStandard;
import uninsubria.utils.languages.Language;

public class Grid {

	private DiceSet dices;
	private String[] diceFaces;
	private Integer[] diceNumb;

	public Grid() {
		dices = new DiceSet();
		diceNumb = dices.getResultNumb();
	}

	public Grid(Language l) {
		dices = new DiceSet(DiceSetStandard.getByLanguage(l.getLanguage()));
		diceNumb = dices.getResultNumb();
	}

	/**
	 * Genera la griglia su cui vengono disposti randomicamente i risultati del lancio dei dadi
	 * in accordo col loro numero.
	 * @return un array di stringhe contenente in maniera randomica i risultati del lancio dei dadi
	 */
	public String[] generateGridString() {
		diceFaces = dices.getResultFaces();
		return diceFaces;
	}

	/**
	 * Genera la griglia su cui vengono disposti randomicamente i numeri dei dadi lanciati, in accordo
	 * col loro risultato.
	 * @return un Array di int contenente in maniera randomica il numero del lancio dei dadi.
	 */
	public Integer[] generateGridNumber() {
		diceNumb = dices.getResultNumb();
		return diceNumb;
	}

	/**
	 * Lancia i dadi purché non siano già stati lanciati o sia avvenuto prima un reset degli stessi.
	 */
	public void throwDices() {
		if(!dices.areThrown()) {
			dices.throwDices();
			dices.randomizePosition();
		}
	}

	/**
	 * Resetta i dadi per prepararli al prossimo lancio.
	 */
	public void resetDices() {
		dices.setNotThrown();
		diceFaces = dices.getResultFaces();
		diceNumb = dices.getResultNumb();
	}

	public String toString() {
		String tmp = "";
		return dices.toString();
	}

	/**
	 * Restituisce i dadi come stringhe posizionati all'interno di un array di stringhe.
	 * @return un array di stringhe contenenti i dadi.
	 */
	public String[] toStringArray() {
		return dices.toStringArray();
	}
}
