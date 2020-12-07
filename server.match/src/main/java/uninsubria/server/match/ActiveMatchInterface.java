package uninsubria.server.match;

public interface ActiveMatchInterface extends MatchInterface {

	public void throwDices();
	public void calculateScore();
	public void conclude();
}
