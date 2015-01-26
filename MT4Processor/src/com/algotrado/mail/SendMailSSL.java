package com.algotrado.mail;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class SendMailSSL {
	public static void main(String[] args) {
//		sendEmail("pinbar-detection-strategy@no-spam.com", "ohadbe@gmail.com", "List of pinbars was updated", "Dear Ohad," +
//				"\n\n A list of pinbars was updated! \nPlease look at your google drive!\n Thanks,\nYour Pinbar Detection System.");
		
		sendEmail("shaniki.biki@no-spam.com", "shanirozin@gmail.com", "List of movies", "Dear Shani," +
				"\n\n What movie do you want to see?");
	}
	/**
	 * Call This method to send email
	 * @param from - who sent the email
	 * @param to - the mail recipient
	 * @param cc - 
	 * @param subject
	 * @param body
	 */
	public static void sendEmail(String from, String to, String subject, String body) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("ohadbehore","Unisfair");
				}
			});
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);
 
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
