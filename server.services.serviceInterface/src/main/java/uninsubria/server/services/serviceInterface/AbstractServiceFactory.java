package uninsubria.server.services.serviceInterface;

public abstract class AbstractServiceFactory {
	/*---Fields---*/
	
	/*---Constructor---*/
	protected AbstractServiceFactory() {}
	
	/*---Methods---*/
	public abstract Service getService(ServiceType st, Object ... fields) throws Exception;
}
