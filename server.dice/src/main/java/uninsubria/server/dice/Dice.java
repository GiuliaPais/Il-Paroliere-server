package uninsubria.server.dice;

import java.util.ArrayList;
import java.util.Random;

public class Dice implements DiceInterface {

	private final int diceNo;
	private final String[] faces;
	private String[] lettersOccurrences;
	private String resultFace;
	private boolean thrown;
	
	/**
	 * Costruttore del dado
	 * @param diceNo: indica il numero identificativo del dado.
	 * @param faces: indica le varie facce del dado contenente le stringhe.
	 */
	public Dice(int diceNo, String[] faces) {
		this.diceNo = diceNo;
		this.faces = faces;
		thrown = false;

		setLettersOccurrences();
	}
	
	/**
	 * Lancia il dado, esclusivamente se non è già stato lanciato, sfruttando come argomento un'istanza di 
	 * random e setta la variabile booleana del lancio a <code>true</code>.
	 */
	@Override
	public void throwDice(Random generator) {
		if(!thrown) {
			int i = faces.length;
			resultFace = faces[generator.nextInt(i)];
			thrown = true;
		}
	}

	/**
	 * restituisce il numero identificativo del dado.
	 */
	@Override
	public int getDiceNo() {
		return diceNo;
	}

	/**
	 * Restituisce come array di stringhe ogni faccia del dado.
	 */
	@Override
	public String[] getFaces() {
		return faces;
	}
	
	/**
	 * Restituisce come stringa il risultato del lancio solo ed esclusivamente se la variabile
	 * booleana thrown � settata su <code>true</code>. Altrimenti restituisce un messaggio.
	 */
	@Override
	public String getResultFace() {
		if(thrown)
			return resultFace;
		else
			return null;
	}

	/**
	 * Restituisce il valore della variabile booleana thrown. <code>true</code> se il dado e' stato
	 * lanciato. <code>False</code> altrimenti.
	 */
	@Override
	public boolean isThrown() {
		return thrown;
	}
	
	/**
	 * Setta nuovamente il valore della variabile booleana thrown a <code>false</code> e la variabile
	 * del risultato a <code>null</code>.
	 */
	public void setNotThrown() {
		thrown = false;
		resultFace = null;
	}

	/**
	 * Restituisce un array di String contenente le singole facce del dado, senza ripetizioni.
	 * @return String[], un array di String con le occorrenze delle lettere.
	 */
	public String[] getLettersOccurrences() {
		return lettersOccurrences;
	}

	/**
	 * Restituisce una stringa contenente la faccia estratta dal lancio ed il numero del dado.
	 */
	public String toString() {
		return resultFace + " " + diceNo;
	}

	// Crea un array contenente le singole facce, senza ripetizioni.
	private void setLettersOccurrences() {
		ArrayList<String> occurrences = new ArrayList<>();

		for(String s : faces) {
			if(!occurrences.contains(s))
				occurrences.add(s);
		}

		lettersOccurrences = new String[occurrences.size()];

		for(int i = 0; i < lettersOccurrences.length; i++)
			lettersOccurrences[i] = occurrences.get(i);
	}
}
