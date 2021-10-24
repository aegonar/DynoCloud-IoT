import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail{
	
	String recipient; 
	String subject; 
	String text;
	
	public SendEmail(String recipient, String subject, String text){
		this.recipient=recipient;
		this.subject=subject;
		this.text=text;
	
	}
	
	public void send(){	
		
		System.out.println("[Mail] ["+recipient+"] ["+subject+"] "+text);
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("dynocloud.capstone@gmail.com","password");
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("dynocloud.capstone@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recipient));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
	}
		
}
