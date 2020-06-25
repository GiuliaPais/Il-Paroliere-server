package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.services.api.Service;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;

public class CreateRoomService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.CREATE_ROOM;
	private String roomName;
	private Integer playersNo;
	
	
	public CreateRoomService(String roomName, Integer playersNo) {
		this.roomName = roomName;
		this.playersNo = playersNo;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public PlayerServiceType getServiceType() {
		return serviceType;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getPlayersNo() {
		return playersNo;
	}

	public void setPlayersNo(Integer playersNo) {
		this.playersNo = playersNo;
	}
	
	


}