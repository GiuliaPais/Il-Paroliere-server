package uninsubria.server.services.api;


import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import uninsubria.server.services.playerServicesImpl.CreateRoomService;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.server.services.serviceInterface.Service;

public class Prova {

	public static void main(String[] args) {
		
		ServiceLoader<Service> loader = ServiceLoader.load(Service.class);
		CreateRoomService selected;
		for (Service s : loader) {
			if (s.getServiceType().equals(PlayerServiceType.CREATE_ROOM)) {
				selected = (CreateRoomService) s;
				System.out.println("Service type: "+ selected.getServiceType());
				System.out.println("Room name: "+ selected.getRoomName());
			}
		}


}}
