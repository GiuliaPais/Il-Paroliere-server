package uninsubria.server.email;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class that provides static methods for the actual process of mail delivery.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.2
 */
public class EmailSender{
	/*it-it(le proprieta' sono state prese dal sito:
	 https://support.microsoft.com//office/impostazioni-di-posta-elettronica-pop-e-imap-per-outlook-8361e398-8af4-4e97-b147-6c6c4ac95353)*/

    private String username;
    private String password;
    private static EmailSender emailSender;

    /**
     * Costruttore privato
     */
    private EmailSender() {
    }

    public static EmailSender getInstance(){
        if (emailSender != null){
            return emailSender;
        } else {
            emailSender = new EmailSender();
            return emailSender;
        }
    }

    /**
     * inizializza l'oggetto EmailSender
     * @param usr
     * @param psw
     */
    public static void initializeEmailSender(String usr, String psw) {
        EmailSender instance = getInstance();
        instance.setUsername(usr);
        instance.setPassword(psw);
    }

    //Password getter and setter
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Username getter and setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Il metodo invia una mail
     * @param email					The email
     * @throws SendFailedException
     * @throws MessagingException
     */
    public static void sendEmail(Email email) throws SendFailedException, MessagingException{
        String provider = "outlook";
        Properties props = getProperties(provider);
        /*Session session = Session.getInstance(props,  new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("Il_Paroliere@outlook.it", "Paroliere21");
            }
        });*/
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailSender.getInstance().getUsername(), EmailSender.getInstance().getPassword());
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(EmailSender.getInstance().getUsername()));
        msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email.getTo()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html; charset=UTF-8");
        // Create a transport.
        Transport transport = session.getTransport();

        // Send the message.
        try
        {
            System.out.println("Sending...");

            // Connect to outlook using the SMTP username and password you specified above. host : smtp-mail.outlook.com
            transport.connect("smtp-mail.outlook.com", "Il_Paroliere@outlook.it", "Paroliere21");

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }

    }

    /**
     * il metodo restituisce le proprieta' delle impostazioni SMTP dei vari provider
     * @param provider		The Provider
     * @return props		The properties of the provider(see line 14)
     */
    public static Properties getProperties(String provider) {

        Properties props = System.getProperties();

        //include Gmail e Aol(Verizon.net) //Could not connect to SMTP host: smtp.gmail.com, port: 465, response: -1
        if(provider.contentEquals("gmail") || provider.contentEquals("aol")) {

            props.put("mail.smtp.host",getHost(provider));
            props.put("mail.smtp.ssl/tls.enable", "true");
            props.put("mail.smtp.port",465);
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
            return props;
        }

        //include Outlook, hotmail, Live
        else if(provider.contentEquals("outlook") || provider.contentEquals("hotmail") || provider.contentEquals("studenti")) {

            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            //props.put("mail.smtp.host", "outlook.office365.com");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "smtp-mail.outlook.com");
            props.put("mail.smtp.port", 587);

            return props;
        }

        //Yaoo
        else if(provider.contentEquals("yahoo")) {

            props.put("mail.smtp.host",getHost(provider));
            props.put("mail.smtp.tls/starttls.enable", "true");
            props.put("mail.smtp.port",587);
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");

            return props;
        }
        return null;
    }

    /**
     * il metodo recupera il provider
     * @param provider			The provider
     * @return host				The server Provider (SMTP Config.)
     */
    public static String getHost(String provider) {

        String host="";

        if(provider.contentEquals("gmail")){
            host="smtp.gmail.com";
            return host;
        }
        //include Outlook, hotmail e Live
        else if(provider.contentEquals("outlook") || provider.contentEquals("hotmail") || provider.contentEquals("studenti")){
            host="smtp.office365.com";
            return host;
        }

        else if(provider.contentEquals("yahoo")){
            host="smtp.mail.yahoo.com ";
            return host;
        }
        else if(provider.contentEquals("aol")){
            host="smtp.aol.com";
            return host;
        }

        return null;
    }
}
