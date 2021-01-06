package uninsubria.server.dice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The type Json dice.
 */
public class JSONDice {

	/**
	 * Instantiates a new Json dice.
	 */
	public JSONDice() {
		
	}

	/**
	 * Trasforma il dado passato come parametro in un oggetto JSONObject.
	 *
	 * @param dice il dado da trasformare in JSONObject.
	 * @return <code>JSONObject</code> del dado.
	 */
	public JSONObject makeJSONFromDice(Dice dice) {
		
		JSONObject obj = new JSONObject();
		JSONObject details = new JSONObject();
		JSONArray faces = new JSONArray();
		
		for(int i = 0; i < dice.getFaces().length; i++) {
			faces.put(dice.getFaces()[i]);
		}
		
		details.put("DiceNo", dice.getDiceNo());
		details.put("Faces", faces);
		
		obj.put("Dice", details);
		
		return obj;
	}

	/**
	 * Trasforma l'array di dadi passati come parametro in un oggetto JSONArray.
	 *
	 * @param dices l'array di dadi da trasformare in JSONArray.
	 * @return <code>JSONArray</code> dei dadi.
	 */
	public JSONArray makeJSONFromDice(Dice[] dices) {
		
		JSONArray array = new JSONArray();
		var obj = new JSONObject();

		for(Dice dice : dices) {
			obj = this.makeJSONFromDice(dice);
			array.put(obj);
		}
		
		return array;
	}

	/**
	 * Trasforma JSONObject passato come parametro in dado.
	 *
	 * @param obj l'oggetto da trasformare in dado.
	 * @return <code>Dice</code> ricavato da JSONObject.
	 */
	public Dice makeDiceFromJSON(JSONObject obj) {
		
		JSONObject details = obj.getJSONObject("Dice");
		JSONArray facesJSON = details.getJSONArray("Faces");
		
		int length = facesJSON.length();
		
		int diceNo = details.getInt("DiceNo");
		String[] faces = new String[length];
		
		for(int i = 0; i < length; i++)
			faces[i] = facesJSON.getString(i);

		return new Dice(diceNo, faces);
		
	}

	/**
	 * Ricava un array di dadi da un JSONArray contenente JSONObject di dadi.
	 *
	 * @param array JSONArray da trasformare in array di dadi.
	 * @return <code>Dice[]</code> contenenti tutti i dadi contenuti in JSONArray.
	 */
	public Dice[] makeDiceFromJSON(JSONArray array) {
		
		int length = array.length();
		Dice[] dices = new Dice[length];
		
		for(int i = 0; i < length; i++) {
			JSONObject obj = array.getJSONObject(i);
			Dice dice = this.makeDiceFromJSON(obj);
			dices[i] = dice;
		}
		
		return dices;
	}

	/**
	 * Ricava un array di dadi da una stringa di un file JSON.
	 *
	 * @param jsonString the json string
	 * @return <code>Dice[]</code> contenenti tutti i dadi contenuti in JSONArray.
	 */
	public Dice[] makeDiceFromJSON(String jsonString) {
		
		JSONArray array = new JSONArray(jsonString);
		
		int length = array.length();
		Dice[] dices = new Dice[length];
		
		for(int i = 0; i < length; i++) {
			JSONObject obj = array.getJSONObject(i);
			Dice dice = this.makeDiceFromJSON(obj);
			dices[i] = dice;
		}
		
		return dices;
	}


	/**
	 * Scrive su un file o lo aggiorna se esistente, l'oggetto JSONObject.
	 *
	 * @param path : il percorso del file.
	 * @param obj  : l'oggetto da trascrivere.
	 * @throws IOException eccezione causata dalla mancanza del path.
	 */
	public void writeJSONFile(URI path, JSONObject obj) throws IOException {
		File file = new File(path);
		
		BufferedWriter buff = new BufferedWriter(new FileWriter(file));
		
		buff.write(obj.toString());
		buff.flush();
		buff.close();
	}

	/**
	 * Scrive su un file o lo aggiorna se esistente, l'oggetto JSONArray.
	 *
	 * @param path  : il percorso del file.
	 * @param array : l'array da trascrivere.
	 * @throws IOException eccezione causata dalla mancanza del path.
	 */
	public void writeJSONFile(URI path, JSONArray array) throws IOException {
		File file = new File(path);
		
		BufferedWriter buff = new BufferedWriter(new FileWriter(file));
		
		buff.write(array.toString());
		buff.flush();
		buff.close();
	}

	/**
	 * Legge da un file JSON le stringhe contenute al suo interno e la restituisce.
	 *
	 * @param path : il percorso del file JSON da leggere.
	 * @return String contenente l'array del file.
	 * @throws IOException eccezione causata dalla mancanza del path.
	 */
	public String readJSONFile(URI path) throws IOException {

		File file = new File(path);
		
		BufferedReader buff = new BufferedReader(new FileReader(file));
		String jsonString = buff.readLine();
		buff.close();
		
		return jsonString;
	}
}
