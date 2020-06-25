package uninsubria.server.services.api;

/**
 * Common interface for Service objects. <br><br>
 * <b>Guidelines for implementing classes</b><br>
 * Any class implementing this interface can have one or multiple constructors but <u>NONE of these should have
 * generic type parameters</u>: this is because of Java type erasure and the impossibility of checking actual types at run time
 * using reflection. So, for instance: <br><br>
 * {@code public class DummyService implements Service} {<br>&nbsp&nbsp&nbsp
 * {@code public DummyService(List<String> list)} {@code {//do something}}<br>} <br><br>
 * is going to produce some unexpected and unpredictable behavior at runtime. If there's a need for such generics to be passed as parameters,
 * wrappers should be used instead. For example: <br><br>
 * {@code public class DummyListStrings} {<br>&nbsp&nbsp&nbsp
 * {@code private List<String> list;}<br>
 * {@code //getter, setters, constructors etc.} <br>}<br><br>  		
 * {@code public class DummyService implements Service} {<br>&nbsp&nbsp&nbsp
 * {@code public DummyService(DummyListStrings)} {@code {//do something}}<br>} <br><br>
 * Use of wrapper types should also be limited to strictly necessary only, distinct multiple parameters are preferred whenever possible: <br><br>
 * {@code public class DummyService implements Service} {<br>&nbsp&nbsp&nbsp
 * {@code public DummyService(String s1, String s2, String s3)} {@code {//do something}}<br>}
 * 
 * 
 * @author Giulia Pais
 * @version 0.9.0
 */
public interface Service {
	//TO CHANGE return value
	public void execute();
}
