package uninsubria.server.email;
public class Email {

    private String username, password, to, subject, body;
    private EmailSender emailSender;

    /**
     * costruttore vuoto
     */
    public Email() {

    }

    /**
     * costruttore
     * @param username					The admin email adress
     * @param password						The password of the admin email
     * @param to						The Destination adress
     * @param subject					The Object of the email
     * @param body						The text of the email
     */
    public Email(String username, String password, String to, String subject, String body) {
        super();
        this.username = username;
        this.password = password;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    /**
     * costruttore
     * @param emailSender				The object EmailSender
     * @param to						The Destination adress
     * @param subject					The Object of the email
     * @param body						The text of the email
     */
    public Email(EmailSender emailSender, String to, String subject, String body) {
        super();
        this.setEmailSender(emailSender);
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    /**
     * username getter and setter
     */
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * password getter and setter
     */
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * to getter and setter
     */
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * subject getter and setter
     */
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * body getter and setter
     */
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * EmailSender getter and setter
     */
    public EmailSender getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
}
