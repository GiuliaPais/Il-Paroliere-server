package uninsubria.server.dice;

import java.util.Arrays;

public class DiceSetTest {

	public static void main(String[] args) {
		
		DiceSet ds = new DiceSet();
		
		String risultato = "";
		
		// lancio dei dadi
		System.out.println("Lancio dei dadi");
		ds.throwDices();
		
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + " ";
		System.out.println(risultato);
		
		// mescola
		System.out.println("Mescola e rilancia");
		ds.randomizePosition();
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + " ";
		System.out.println(risultato);
		
		// rilancia senza reset
		System.out.println("Rilancia senza reset");
		ds.throwDices();
		
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + " ";
		System.out.println(risultato);

		// Metodo toString
		System.out.println("Metodo toString()");
		System.out.println(ds.toString());

		// Metodo toStringArray
		System.out.println("Metodo toStringArray()");
		String[] array = ds.toStringArray();
		System.out.println(Arrays.toString(array));

		// Controllo delle occorrenze
		System.out.println("Controllo delle occorrenze");
		String[] occorrenze = ds.getLettersOccurrences();

		for(String s: occorrenze)
			System.out.print(s + " ");

		System.out.println();

		// Settaggio di una nuova lingua
		System.out.println("Settaggio nuova lingua + controllo nuove occorrenze");
		ds.setDiceSetStandard(DiceSetStandard.ENGLISH);

		// Controllo delle nuove occorrenze
		occorrenze = ds.getLettersOccurrences();

		for(String s: occorrenze)
			System.out.print(s + " ");

	}

}
