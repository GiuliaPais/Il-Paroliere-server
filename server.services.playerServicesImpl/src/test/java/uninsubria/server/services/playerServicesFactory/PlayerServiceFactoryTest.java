package uninsubria.server.services.playerServicesFactory;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import uninsubria.server.services.playerServicesImpl.CreateRoomService;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.server.services.serviceInterface.Service;

class PlayerServiceFactoryTest {
	
	private PlayerServiceFactory fact = new PlayerServiceFactory();
	
	@Test
	void testGetService_correct1() throws Exception {
		Service result = fact.getService(PlayerServiceType.CREATE_ROOM);
		assertEquals(CreateRoomService.class, result.getClass());
	}


}
