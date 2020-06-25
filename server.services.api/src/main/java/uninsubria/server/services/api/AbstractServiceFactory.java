package uninsubria.server.services.api;

/**
 * Represents an abstract factory of services.
 * 
 * @author Giulia Pais
 * @version 0.9.0
 */
public abstract class AbstractServiceFactory {
	/*---Fields---*/
	protected ParameterChecker paramChecker;	
	
	/*---Constructor---*/
	protected AbstractServiceFactory() {
		this.paramChecker = new ParameterChecker();
	}
	
	/*---Methods---*/
	/**
	 * Returns a Service object given the type and a set of parameters
	 * @param st the type of the service requested
	 * @param fields a set of parameters necessary to construct the requested service
	 * @return a Service or null if errors occurred
	 */
	public Service getService(ServiceType st, Object ... fields) {
		try {
			Service res = paramChecker.lookupService(st, fields);
			return res;
		} catch (Exception e) {
			return null;
		}
	}
}
