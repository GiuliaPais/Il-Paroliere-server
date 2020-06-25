package uninsubria.server.services.api;

import uninsubria.server.services.api.Service;
import uninsubria.server.services.api.ServiceType;

public class ServiceTestStub2 implements Service {
	/*---Fields---*/
	private String s;
	private Integer i;
	private TestSubClassStub test;
	
	/*---Constructors---*/
	//Contains multiple constructors
	public ServiceTestStub2() {
		
	}
	
	public ServiceTestStub2(String s, Integer i) {
		this.s = s;
		this.i = i;
	}
	
	public ServiceTestStub2(Integer i) {
		this.i = i;
	}
	
	public ServiceTestStub2(TestSubClassStub superC) {
		this.test = superC;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public String getS() {
		return s;
	}

	public Integer getI() {
		return i;
	}

	public TestSubClassStub getTest() {
		return test;
	}
	
	
}
