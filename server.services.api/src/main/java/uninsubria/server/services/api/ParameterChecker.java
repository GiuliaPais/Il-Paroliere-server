package uninsubria.server.services.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

/**
 * Objects of this class are used to obtain appropriate Service instances given their type and appropriate parameters.
 * It also checks, before constructing the actual Services, if the given parameters can be passed to one of the existing 
 * constructors of the target class.<br>
 * 
 * Please refer to the {@link Service} class documentation for more details and guidelines for Services constructors.
 * 
 * 
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class ParameterChecker {
	
	/*---Fields---*/
	private Constructor<?> foundMatch;
	
	/*---Constructors---*/
	/**
	 * Returns a ParameterChecker.
	 */
	public ParameterChecker() {
		foundMatch = null;
	}
	
	/*---Methods---*/
	/**
	 * Checks if the set of provided parameters is usable for constructing a particular service type by checking
	 * all the constructors in that class. If a constructor is found a reference to the constructor object is saved
	 * for later use.
	 * 
	 * @param type a ServiceType for which checking is needed. Should not be null.
	 * @param givenPar the given parameters as an array of Object. Should not be null.
	 * @return true if there is at least one constructor of the ServiceType that takes givenPar as parameters, false otherwise
	 */
	boolean check(ServiceType type, Object[] givenPar) {
		/*If one or both parameters are null should return false*/
		if (type == null | givenPar == null) {
			return false;
		}
		/*Get all the constructors for the Service class corresponding to the given ServiceType*/
		Constructor<?>[] constr = type.getValue().getConstructors();
		for (Constructor<?> c : constr) {
			/*For each constructor get the parameters*/
			Parameter[] cParam = c.getParameters();
			if (cParam.length != givenPar.length) {
				/*If the parameters provided and the constructor parameters differ in length there is a mismatch
				 * and the loops continue to evaluate the next constructor*/
				continue;
			} else if (cParam.length == 0 & givenPar.length == 0) {
				/*If there is a constructor with no arguments and no arguments are provided true is returned*/
				foundMatch = c;
				return true;
			} else {
				int i = 0; /*Keeps track of the parameter number*/
				boolean allMatch = false;
				for (Parameter parameter : cParam) {
					Class<?> paramClass = parameter.getType();
					if (givenPar[i] == null || paramClass.isInstance(givenPar[i]) || paramClass.isAssignableFrom(givenPar[i].getClass())) {
							allMatch = true;
							i++;
						} else {
							allMatch = false;
							break;
						}
					}
				if (allMatch) {
					foundMatch = c;
					return true;
				}
			}
		}
		foundMatch = null;
		return false;
	}
	
	/**
	 * Returns an object of type Service by specifying the ServiceType and a set of parameters necessary for the Service construction.
	 * 
	 * @param type the ServiceType of the Service needed. Must not be null.
	 * @param givenPar the set of parameters to pass to the Service constructor. Must not be null.
	 * @return a Service object of the specified ServiceType if the checking of the parameters using {@link #check} method returns true, null otherwise.
	 * @throws InstantiationException if, for some reason, the Service could not be instantiated using reflection API
	 * @throws IllegalAccessException see {@link IllegalAccessException} for more details
	 * @throws IllegalArgumentException should never be thrown since parameters are checked before calling constructor. Could be thrown if Services constructors
	 * guidelines are not observed. Please see {@link Service} documentation before creating new services.
	 * @throws InvocationTargetException see {@link InvocationTargetException} for more details
	 */
	public Service lookupService(ServiceType type, Object[] givenPar) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (type == null || givenPar == null) {
			return null;
		}
		boolean isCheckOk = check(type, givenPar);
		if(!isCheckOk) {
			return null;
		} else {
			Object serv = foundMatch.newInstance(givenPar);
			if (serv instanceof Service) {
				Service result = (Service) serv;
				return result;
			}
			return null;
		}
	}
	
}
