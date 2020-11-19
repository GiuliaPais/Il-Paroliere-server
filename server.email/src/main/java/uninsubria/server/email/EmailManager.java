package uninsubria.server.email;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class EmailManager{
	
	private final int passwordSize = 12;//abbastanza improvbabile che un codice di 12 crifre di un alfabeto
	private String new_psw;
	
	/**
	 * create EmailManger
	 *
	 */
	public EmailManager() {
	}
	
	/**
	 * create a Thread and start
	 * @param psw 				The new password of a player 
	 */
	public EmailManager(String psw) {
		this.new_psw = psw;
	}
	
	// new_psw getter and setter
	public String getNew_psw() {
		return new_psw;
	}

	public void setNew_psw(String new_psw) {
		this.new_psw = new_psw;
	}
	
	// get the passwordSize
	public int getPasswordSize() {
		return passwordSize;
	}
	
	/**
	 * Il metodo invia un codice di attivazione ad un player che vuole effettuare l'operazione di registrazione
	 * @param email						The Email (the Object of the Email class)
	 * 
	 * @return code						The activation code that allows the player to register
	 * 
	 * @throws SendFailedException
	 * @throws MessagingException
	 */
	public boolean sendActivationCode(Email email, String code) throws SendFailedException, MessagingException {
		
		String subject = "Activation Code";
		String body = "Code: " + code;
		
		email.setSubject(subject);
		email.setBody(body);
		EmailSender.sendEmail(email);
		
		return false;
	}
	
	/**
	 * Il metodo invia una notifica riguardo alla modifica della password
	 * @param email						The Email (the Object of the Email class)
	 * 						
	 * @throws SendFailedException
	 * @throws MessagingException
	 */
	public void sendModNotification(Email email) throws SendFailedException, MessagingException {
		
		String subject = "Password has been changed";
		String body = "Password has been changed";
		
		//bisogna salvare la password
		email.setSubject(subject);
		email.setBody(body);
		EmailSender.sendEmail(email);
		
	}
	
	/**
	 * Il metodo invia una notifica riguardo alla modifica della password temporanea
	 * @param email						The Email (the Object of the Email class)
	 * 
	 * @throws SendFailedException
	 * @throws MessagingException
	 * 
	 * @return da modificare
	 */
	public String sendTemporaryPassword(Email email) throws SendFailedException, MessagingException {
		
		String tmp_psw = getNewAlphaNumeric();
		
		String subject = "Temporary password";
		String body = "You only have 10 minutes to complete registration whit this temporary password: " + tmp_psw;
		
		email.setSubject(subject);
		email.setBody(body);
		EmailSender.sendEmail(email);
		
		System.out.println(tmp_psw);
		
		return tmp_psw;
	}
	
	/**
	 * Il metodo restituisce una stringa alfanumerica casuale
	 * @param passwordSize 
	 * @return una stringa randomica
	 */
	synchronized public String getNewAlphaNumeric() {
	    //alphabet contiene tutti i possibili caratteri che comporranno la Password
		String alphabet = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz?!<>-*[]{}/";
		int alphabetLength = alphabet.length();
		String randomSting = "";
		for (int i = 0; i < getPasswordSize(); i++) {
		    // Scelgo una delle lettere dell'alfabeto.
		    int randomIndexCharInAlphabet = (int)(Math.random()*alphabetLength);
		    randomSting += alphabet.charAt(randomIndexCharInAlphabet);
		}
		return randomSting;
	}
}
