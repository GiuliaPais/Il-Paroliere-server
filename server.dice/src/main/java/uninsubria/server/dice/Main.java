package uninsubria.server.dice;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

	public static void main(String[] args) throws IOException, URISyntaxException {
		
		DiceSet ds = new DiceSet();
				
		String risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + ", ";
		
		System.out.println("Dadi lanciati? " + ds.areThrown());
		System.out.println("Risultato: " + risultato);
		
		// lancio dei dadi
		ds.throwDices();
		
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + ", ";
		
		System.out.println("Dadi lanciati? " + ds.areThrown());
		System.out.println("Risultato: " + risultato);
		
		// reset dei dadi
		ds.setNotThrown();
		
		risultato = "";
		for(String s : ds.getResultFaces())
			risultato += s + ", ";
		
		System.out.println("Dadi lanciati? " + ds.areThrown());
		System.out.println("Risultato: " + risultato);
		
		
	}

}
