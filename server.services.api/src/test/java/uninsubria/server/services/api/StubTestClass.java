package uninsubria.server.services.api;

import java.util.List;

/**
 * Dummy class for testing ParamCheck class
 * @author Giulia Pais
 *
 */
class StubTestClass {
	/*---Fields---*/
	private String name;
	private Integer n;
	private List<String> list;
	
	
	/*---Constructors---*/
	//1 : not used - constructors of services should never have primitive types as parameters
	/*public StubTestClass(String name, int n, List<String> list) {
		this.name = name;
		this.n = n;
		this.list = list;
	}*/
	
	//2
	public StubTestClass(String name, Integer n, List<String> list) {
		this.name = name;
		this.n = n;
		this.list = list;
	}
	
	//3 empty constr
	public StubTestClass() {}
	
}
