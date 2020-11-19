package uninsubria.server.email; /**
 * 
 */

import uninsubria.server.email.EmailManager;

import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * @author Alessandro
 *
 */
public class ProvaEmail {
	
	public static void main(String[] argv) {
	    try {
			String password="";
			String username="";
			String subject="";
			String to="";
			String body="";
		    
		    final JTextField uf= new JTextField(EmailSender.Instance().getUsername());
		    final JPasswordField pf = new JPasswordField(EmailSender.Instance().getPassword());
		    final JTextField tf= new JTextField("alessandrolerro1e@gmail.com");
		    final JTextField sf= new JTextField("email subject");
		    final JTextArea bf= new JTextArea(null,"textual content of the email", 10,20);
		      
		    Object[] message = {
		  	    "Username / From:", uf,
		        "Password:", pf,
		        "To:", tf,
		        "Subject:", sf,
		        "Body:",bf
		    };

		    EmailManager emailManager = new EmailManager();
		    int option = JOptionPane.showOptionDialog( null, message, "Send email", 
		    		JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Send", "Cancel"}, "Send");
		    if (option== JOptionPane.YES_OPTION){ 
		        password= new String( pf.getPassword());
		        username= new String(uf.getText());
		        to=new String(tf.getText());
		        subject=new String(sf.getText());
		        body=new String(bf.getText());
		        Email email = new Email(username, password, to, subject, body);
		        emailManager.sendTemporaryPassword(email);
		    }
		    
		} catch (MessagingException e) {
		    System.err.println("SMTP SEND FAILED:");
		    System.err.println(e.getMessage());
		    e.printStackTrace();
			
		}
	}
	
}
