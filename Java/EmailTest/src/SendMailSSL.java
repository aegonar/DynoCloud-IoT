
public class SendMailSSL {
	public static void main(String[] args) {
		
	SendEmail sendEmail = new SendEmail("agonar@gmail.com", "Welcome to DynoCloud!", "Enjoy your stay!");
	sendEmail.send();
			
	}
}