package uninsubria.server.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class that provides static methods for the actual process of mail delivery.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
class EmailSender{
	/*(le propriet� sono state prese dal sito:
	 https://support.microsoft.com/it-it/office/impostazioni-di-posta-elettronica-pop-e-imap-per-outlook-8361e398-8af4-4e97-b147-6c6c4ac95353)*/

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
        String provider = getInstance().username.substring(getInstance().username.indexOf("@") + 1, getInstance().username.indexOf("."));
        Properties props = getProperties(provider);
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(getInstance().username));
        msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email.getTo(), false));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html; charset=UTF-8");
        Transport.send(msg,getInstance().username,getInstance().password);
    }

    /**
     * il metodo restituisce le propriet� delle impostazioni SMTP dei vari provider
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

            return props;
        }

        //include Outlook, hotmail, Live
        else if(provider.contentEquals("outlook") || provider.contentEquals("hotmail") || provider.contentEquals("studenti")) {

            props.put("mail.smtp.host", getHost(provider));
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port",587);

            return props;
        }

        //Yaoo
        else if(provider.contentEquals("yahoo")) {

            props.put("mail.smtp.host",getHost(provider));
            props.put("mail.smtp.tls/starttls.enable", "true");
            props.put("mail.smtp.port",587);

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
