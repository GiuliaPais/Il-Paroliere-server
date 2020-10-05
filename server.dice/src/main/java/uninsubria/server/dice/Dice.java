package uninsubria.server.dice;

import java.util.Random;

public class Dice implements DiceInterface {

	private int diceNo;
	private String[] faces;
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
	}
	
	/**
	 * Lancia il dado sfruttando come argomento un'istanza di random e setta la variabile booleana
	 * del lancio a <code>true</code>.
	 */
	@Override
	public void throwDice(Random generator) {
		int i = faces.length;
		resultFace = faces[generator.nextInt(i)];
		thrown = true;
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
		if(thrown == true)
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
	 * Restituisce una stringa contenente il numero ed i singoli valori delle facce del dado.
	 */
	public String toString() {
		String s = "Dice num: " + diceNo + "\n";
		for(int i = 0; i < faces.length; i++) {
			if(i < faces.length -1)
				s += "Face" + (i+1) + ": " + faces[i] + "; ";
			else
				s += "Face" + (i+1) + ": " + faces[i] + ".\n";
		}
		return s;
	}
}
