package uninsubria.server.services.api;

/**
 * The ServiceType interface is designed to be implemented by enum classes. Objects implementing this interface are responsible
 * for correct indexing of available services.
 * 
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public interface ServiceType {
	/**
	 * Gets the Class object associated with this ServiceType. 
	 * @return a Class object associated with this type
	 */
	Class<?> getValue();

}
