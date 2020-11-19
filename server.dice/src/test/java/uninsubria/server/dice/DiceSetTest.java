package uninsubria.server.dice;

public class DiceSetTest {

	public static void main(String[] args) {
		
		DiceSet ds = new DiceSet();
		
		String risultato = "";
		
		// lancio dei dadi
		ds.throwDices();
		
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + " ";
		System.out.println(risultato);
		
		// mescola
		ds.randomizePosition();
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + " ";
		System.out.println(risultato);
		
		// rilancia senza reset
		ds.throwDices();
		
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + " ";
		System.out.println(risultato);

		// Metodo toString
		System.out.println(ds.toString());

		// Controllo delle occorrenze
		String[] occorrenze = ds.getLettersOccurrences();

		for(String s: occorrenze)
			System.out.print(s + " ");

		System.out.println();

		// Settaggio di una nuova lingua
		ds.setDiceSetStandard(DiceSetStandard.ENGLISH);

		// Controllo delle nuove occorrenze
		occorrenze = ds.getLettersOccurrences();

		for(String s: occorrenze)
			System.out.print(s + " ");

	}

}
