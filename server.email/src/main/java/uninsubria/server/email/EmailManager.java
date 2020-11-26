package uninsubria.server.email;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import java.util.UUID;

/**
 * Entry point of the email module, offers API to other modules.
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
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
	 * Sends an email to the selected recipient with the activation code for the user registration process.
	 *
	 * @param recipient the recipient
	 * @param code      the code
	 * @throws SendFailedException
	 * @throws MessagingException
	 */
	public void sendActivationCode(String recipient, UUID code) throws SendFailedException, MessagingException {
		String subject = "Il Paroliere - Activation Code";
		String body_ita = "<b>Hai ricevuto questa email in seguito alla richiesta di registrazione per 'Il Paroliere'.</b><br>" +
				"Se non hai effettuato nessuna richiesta ti preghiamo di ignorare questa email.<br>" +
				"Se hai effettuato la richiesta inserisci questo codice di attivazione nell'apposita schermata.<br>" +
				"Il codice scadr\u00e0 dopo 10 minuti dall'invio di questa mail" +
				"<hr>";
		String body_eng = "<b>You received this email for the registration procedure of 'Il Paroliere'.</b><br>" +
				"If you did not send any request, please ignore this email.<br>" +
				"If you sent the request, please insert this activation code in the appropriate window<br>" +
				"The code will expire after 10 minutes this email was sent" +
				"<br><br>";
		String body_code = "<p style=\"text-align:center\">"+ code +"</p>";
		String body = body_ita + body_eng + body_code;
		Email email = new Email();
		email.setSubject(subject);
		email.setBody(body);
		email.setTo(recipient);
		EmailSender.sendEmail(email);
	}

	public static void initializeEmailManager(String email, String password) {
		EmailSender.initializeEmailSender(email, password);
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
