package uninsubria.server.email;

import javax.mail.MessagingException;
import java.util.UUID;

/**
 * Entry point of the email module, offers API to other modules.
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.3
 */
public class EmailManager{

	/**
	 * Instantiates a new Email manager.
	 */
	public EmailManager() {
	}


	/**
	 * Sends an email to the selected recipient with the activation code for the user registration process.
	 *
	 * @param recipient the recipient
	 * @param code      the code
	 * @throws MessagingException the messaging exception
	 */
	public void sendActivationCode(String recipient, UUID code) throws MessagingException {
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

	/**
	 * Initialize email manager.
	 *
	 * @param email    the email
	 * @param password the password
	 */
	public static void initializeEmailManager(String email, String password) {
		EmailSender.initializeEmailSender(email, password);
	}

	/**
	 * Sends a notification email for password changed.
	 * @param recipient The email recipient
	 * @throws MessagingException the messaging exception
	 */
	public void sendModNotification(String recipient) throws MessagingException {
		String subject = "Il Paroliere - Password changed";
		String body_ita = "<b>Hai ricevuto questa email in seguito alla richiesta di cambio password per 'Il Paroliere'.</b><br>" +
				"La password è stata modificata con successo." +
				"<hr>";
		String body_eng = "<b>You received this email for the password change procedure of 'Il Paroliere'.</b><br>" +
				"The password was correctly modified." +
				"<br><br>";

		String body = body_ita + body_eng;
		Email email = new Email();
		email.setSubject(subject);
		email.setBody(body);
		email.setTo(recipient);
		EmailSender.sendEmail(email);
	}


	/**
	 * Sends a temporary password when user requests password reset.
	 *
	 * @param recipient the recipient
	 * @param newPw     the new pw
	 * @throws MessagingException the messaging exception
	 */
	public void sendTemporaryPassword(String recipient, UUID newPw) throws MessagingException {
		String subject = "Il Paroliere - Password reset";
		String body_ita = "<b>Hai ricevuto questa email in seguito alla richiesta di reset della password per 'Il Paroliere'.</b><br>" +
				"Di seguito trovi la password generata automaticamente, è fortemente consigliato cambiarla al primo accesso." +
				"<hr>";
		String body_eng = "<b>You received this email for the password reset procedure of 'Il Paroliere'.</b><br>" +
				"You can find the automatically generated new password below, we strongly reccommend to change it at the first login." +
				"<br><br>";
		String body = body_ita + body_eng + newPw;
		Email email = new Email();
		email.setSubject(subject);
		email.setBody(body);
		email.setTo(recipient);
		EmailSender.sendEmail(email);
	}

}
