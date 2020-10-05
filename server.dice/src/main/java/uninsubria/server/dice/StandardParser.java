package uninsubria.server.dice;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONArray;

public class StandardParser {
	
	private URI path;
	private Dice[] dices;
	
	public StandardParser(DiceSetStandard diceSet) throws IOException, URISyntaxException {
		path = this.getClass().getClassLoader().getResource(diceSet.getFileName()).toURI();
		
		this.readJSONFile(path);
	}
	
	private void readJSONFile(URI path) throws IOException {
		
		if(!new File(path).exists())
			this.writeJSONFile(path);
		
		JSONDice jd = new JSONDice();
		String jsonString = jd.readJSONFile(path);
		dices = jd.makeDiceFromJSON(jsonString);
		
	}
	
	private void writeJSONFile(URI path) throws IOException {
		
		JSONDice jd = new JSONDice();
		
		Dice[] dices = this.setDefaultDice();
		JSONArray array = jd.makeJSONFromDice(dices);
		
		jd.writeJSONFile(path, array);
	}
	
	public Dice[] setDefaultDice() {
		
		String[] faces1 = {"B", "A", "O", "O", "Qu", "M"};
		String[] faces2 = {"U", "T", "E", "S", "L", "P"};
		String[] faces3 = {"I", "G", "E", "N", "V", "T"};
		String[] faces4 = {"O", "U", "L", "I", "E", "R"};
		String[] faces5 = {"A", "C", "E", "S", "L", "R"};
		String[] faces6 = {"R", "A", "T", "I", "B", "L"};
		String[] faces7 = {"S", "M", "I", "R", "O", "A"};
		String[] faces8 = {"I", "S", "E", "E", "F", "H"};
		String[] faces9 = {"S", "O", "T", "E", "N", "D"};
		String[] faces10 = {"A", "I", "C", "O", "F", "R"};
		String[] faces11 = {"V", "N", "Z", "D", "A", "E"};
		String[] faces12 = {"I", "E", "A", "T", "A", "O"};
		String[] faces13 = {"O", "T", "U", "C", "E", "N"};
		String[] faces14 = {"N", "O", "L", "G", "U", "E"};
		String[] faces15 = {"D", "C", "M", "P", "A", "E"};
		String[] faces16 = {"E", "R", "I", "N", "S", "H"};
		
		Dice dice1 = new Dice(1, faces1);
		Dice dice2 = new Dice(2, faces2);
		Dice dice3 = new Dice(3, faces3);
		Dice dice4 = new Dice(4, faces4);
		Dice dice5 = new Dice(5, faces5);
		Dice dice6 = new Dice(6, faces6);
		Dice dice7 = new Dice(7, faces7);
		Dice dice8 = new Dice(8, faces8);
		Dice dice9 = new Dice(9, faces9);
		Dice dice10 = new Dice(10, faces10);
		Dice dice11 = new Dice(11, faces11);
		Dice dice12 = new Dice(12, faces12);
		Dice dice13 = new Dice(13, faces13);
		Dice dice14 = new Dice(14, faces14);
		Dice dice15 = new Dice(15, faces15);
		Dice dice16 = new Dice(16, faces16);
		
		Dice[] array = {dice1, dice2, dice3, dice4, dice5, 
				dice6, dice7, dice8, dice9, dice10, dice11, dice12, dice13, dice14,
				dice15, dice16};
		
		return array;
	}
	
	public Dice[] getDices() {
		return dices;
	}
}
