package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.services.api.Service;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;
import uninsubria.utils.serviceResults.ServiceResultInterface;

public class CreateRoomService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.CREATE_ROOM;
	private String roomName;
	private Integer playersNo;
	private Ruleset ruleset;
	private Language language;
	
	
	public CreateRoomService(String roomName, Integer playersNo, Ruleset ruleset, Language language) {
		this.roomName = roomName;
		this.playersNo = playersNo;
		this.language = language;
		this.ruleset = ruleset;
	}

	@Override
	public ServiceResultInterface execute() {
		// TODO Auto-generated method stub
		return null;
	}
	
	


}
