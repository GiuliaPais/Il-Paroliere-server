package uninsubria.server.services.api;

import uninsubria.server.services.api.ServiceType;

public enum ServiceTypeStub implements ServiceType {
	
	TEST1 (ServiceTestStub1.class),
	TEST2 (ServiceTestStub2.class);
	
	public Class<?> value;
	
	ServiceTypeStub(Class<?> class1) {
		value = class1;
	}

	@Override
	public Class<?> getValue() {
		return value;
	}

}
