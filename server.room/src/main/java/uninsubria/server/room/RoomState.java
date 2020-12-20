package uninsubria.server.room;

public enum RoomState {

	OPEN("Open"),
	FULL("Full"),
	GAMEON("Game on"),
	TIMEOUT("Time out");

	private final String NAME;

	private RoomState(String name) {
		NAME = name;
	}

	public String getName() {
		return NAME;
	}

	public static RoomState getById(int id) {
		for(RoomState rs : RoomState.values()) {
			if(id == rs.ordinal()) {
				return rs;
			}
		}
		return null;
	}

	public String toString() {
		return "Room state: " + NAME;
	}

}
