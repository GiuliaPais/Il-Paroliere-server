package uninsubria.server.services.playerServicesFactory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import uninsubria.server.services.playerServicesTypes.PlayerServiceType;

class ParameterCheckerTest {
	
	private ParameterChecker checker = new ParameterChecker();
	
	@Test
	void testCheck_allNull() {
		boolean result = checker.check(null, null);
		assertFalse(result);
	}
	
	@Test
	void testCheck_anyNull() {
		boolean result = checker.check(PlayerServiceType.CREATE_ROOM, null);
		assertFalse(result);
	}
	
	@Test
	void testCheck_success2Param() {
		Object[] given = new Object[2];
		given[0] = "room1";
		given[1] = 4;
		boolean result = checker.check(PlayerServiceType.CREATE_ROOM, given);
		assertTrue(result);
	}
	
	@Test
	void testCheck_givenContainsNull() {
		Object[] given = new Object[2];
		given[0] = null;
		given[1] = 4;
		boolean result = checker.check(PlayerServiceType.CREATE_ROOM, given);
		assertTrue(result);
	}
	
	
}
