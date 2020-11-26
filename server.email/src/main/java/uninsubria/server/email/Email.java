package uninsubria.server.email;

/**
 * Represents an email object.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
class Email {

    private String to, subject, body;

    /**
     * Instantiates a new Email.
     */
    public Email() {}

    /**
     * Instantiates a new Email.
     *
     * @param to      the recipient of the email
     * @param subject the subject of the email
     * @param body    the body of the email
     */
    public Email(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    /**
     * Gets the value of to.
     *
     * @return the to value
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets to value.
     *
     * @param to the to value
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets the value of subject.
     *
     * @return the subject value
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets subject value.
     *
     * @param subject the subject value
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the value of body
     *
     * @return the body value
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets body value.
     *
     * @param body the body value
     */
    public void setBody(String body) {
        this.body = body;
    }

}
