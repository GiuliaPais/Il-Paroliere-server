package uninsubria.server.services.api;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

import uninsubria.server.services.api.ParameterChecker;
import uninsubria.server.services.api.Service;

class ParameterCheckerTest {
	
	private ParameterChecker checker = new ParameterChecker();

	
	@Test
	void testCheck_allNull() {
		boolean result = checker.check(null, null);
		assertFalse(result);
	}
	
	@Test
	void testCheck_anyNull() {
		boolean result = checker.check(ServiceTypeStub.TEST1, null);
		assertFalse(result);
	}
	
	@Test
	void testCheck_success2Param() {
		Object[] given = new Object[2];
		given[0] = "room1";
		given[1] = 4;
		boolean result = checker.check(ServiceTypeStub.TEST1, given);
		assertTrue(result);
	}
	
	@Test
	void testCheck_givenContainsNull() {
		Object[] given = new Object[2];
		given[0] = null;
		given[1] = 4;
		boolean result = checker.check(ServiceTypeStub.TEST1, given);
		assertTrue(result);
	}
	
	@Test
	void testCheck_numberMismatch() {
		Object[] given = new Object[1];
		given[0] = "room1";
		boolean result = checker.check(ServiceTypeStub.TEST1, given);
		assertFalse(result);
	}
	
	@Test
	void testCheck_multipleConstructors_noMatch() {
		Object[] given = new Object[1];
		given[0] = "room1"; //list of parameters passed composed only by 1 string
		boolean result = checker.check(ServiceTypeStub.TEST2, given);
		assertFalse(result);
	}
	
	@Test
	void testCheck_multipleConstructors_matchFirst() {
		Object[] given = new Object[0]; //to match empty constructor
		boolean result = checker.check(ServiceTypeStub.TEST2, given);
		assertTrue(result);
	}
	
	@Test
	void testCheck_multipleConstructors_matchSecond() {
		Object[] given = new Object[2];
		given[0] = "room1";
		given[1] = 1;
		boolean result = checker.check(ServiceTypeStub.TEST2, given);
		assertTrue(result);
	}
	
	@Test
	void testCheck_multipleConstructors_matchThird() {
		Object[] given = new Object[1];
		given[0] = 1;
		boolean result = checker.check(ServiceTypeStub.TEST2, given);
		assertTrue(result);
	}
	

	@Test
	void testCheck_multipleConstructors_assignableFromSuperclass() {
		Object[] given = new Object[1];
		given[0] = new TestSuperClassStub();
		boolean result = checker.check(ServiceTypeStub.TEST2, given);
		assertFalse(result);
	}
	
	@Test
	void testCheck_multipleConstructors_assignableFromSubclass() {
		Object[] given = new Object[1];
		given[0] = new TestSubClassStub2();
		boolean result = checker.check(ServiceTypeStub.TEST2, given);
		assertTrue(result);
	}
	
	@Test
	void testLookupService_1Const_ok() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] given = new Object[2];
		given[0] = "room1";
		given[1] = 4;
		Service result = checker.lookupService(ServiceTypeStub.TEST1, given);
		assertEquals(ServiceTestStub1.class, result.getClass());
	}
	
	@Test
	void testLookupService_1Const_KO() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] given = new Object[2];
		given[0] = 1;
		given[1] = 4;
		Service result = checker.lookupService(ServiceTypeStub.TEST1, given);
		assertEquals(null, result);
	}
	
	@Test
	void testLookupService_anyNull() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] given = null;
		Service result = checker.lookupService(ServiceTypeStub.TEST1, given);
		assertEquals(null, result);
	}
	
	@Test
	void testLookupService_2Const_ok_2param() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] given = new Object[2];
		given[0] = "room1";
		given[1] = 4;
		Service result = checker.lookupService(ServiceTypeStub.TEST2, given);
		assertEquals(ServiceTestStub2.class, result.getClass());
		ServiceTestStub2 resCast = (ServiceTestStub2) result;
		assertEquals(given[0], resCast.getS());
		assertEquals(given[1], resCast.getI());
	}
	
	@Test
	void testLookupService_2Const_ok_1paramObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] given = new Object[1];
		given[0] = new TestSubClassStub2();
		Service result = checker.lookupService(ServiceTypeStub.TEST2, given);
		assertEquals(ServiceTestStub2.class, result.getClass());
		ServiceTestStub2 resCast = (ServiceTestStub2) result;
		assertEquals(given[0], resCast.getTest());
	}
	
	@Test
	void testLookupService_2Const_KO_1paramObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] given = new Object[1];
		given[0] = new TestSuperClassStub();
		Service result = checker.lookupService(ServiceTypeStub.TEST2, given);
		assertEquals(null, result);
	}
	
}