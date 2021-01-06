package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.roomlist.RoomList;
import uninsubria.server.services.api.Service;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.serviceResults.ServiceResultInterface;


/**
 * Implementation of service responsible for creating a new room.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class CreateRoomService implements Service {

	private final PlayerWrapper playerWrapper;
	private final Lobby lobby;

	/**
	 * Instantiates a new Create room service.
	 *
	 * @param playerWrapper the player wrapper
	 * @param lobby         the lobby
	 */
	public CreateRoomService(PlayerWrapper playerWrapper, Lobby lobby) {
		this.playerWrapper = playerWrapper;
		this.lobby = lobby;
	}

	@Override
	public ServiceResultInterface execute() {
		RoomList.createRoom(playerWrapper, lobby);
		return null;
	}
}
