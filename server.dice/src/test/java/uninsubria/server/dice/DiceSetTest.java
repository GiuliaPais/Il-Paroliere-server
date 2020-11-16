package uninsubria.server.dice;

public class DiceSetTest {

	public static void main(String[] args) throws Exception {
		
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

		System.out.println(ds.toString());

	}

}
