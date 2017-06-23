package it.IotServer.Utility;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class GoogleMail {
	private static String host = "smtp.gmail.com";
	private static String from = "iotappunivpm@gmail.com";
	private static String password = "IotAppPassword";
	private GoogleMail() {

	}

	public static void send(String to, String sub, String msg) {

		Properties props = new Properties();
		
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", password);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(sub);
			message.setText(msg);

			Transport transport = session.getTransport("smtp");
	        // System.out.println("success point 5");

	        transport.connect(host, from, password);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}
}