package uninsubria.server.dice;

public enum DiceSetStandard {
	
	STANDARD("Standard", "DiceSetFaces_STANDARD.json"), 
	ITALIAN("Italian", "DiceSetFaces_ITA.json");
	
	private String language, fileName;
	
	private DiceSetStandard(String language, String fileName) {
		this.language = language;
		this.fileName = fileName;
	}
	
	public String getLanguage() {
		return language;
	}

	public String getFileName() {
		return fileName;
	}

	public String toString() {
		return language + " dices";
	}
	
}
