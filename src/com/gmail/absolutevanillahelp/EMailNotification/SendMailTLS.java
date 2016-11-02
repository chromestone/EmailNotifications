package com.gmail.absolutevanillahelp.EMailNotification;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailTLS {

	private final String username;
	private final String password;
	private Properties mailProps;

	public SendMailTLS(String username, String password) {
		this.username = username;
		this.password = password;
		
		mailProps = new Properties();
		mailProps.put("mail.smtp.auth", "true");
		mailProps.put("mail.smtp.starttls.enable", "true");
		mailProps.put("mail.smtp.host", "smtp.gmail.com");
		mailProps.put("mail.smtp.port", "587");
	}
	
	public boolean send(String subject, String text, String address) {
		boolean sent = false;
		
		try {
			Message message = new MimeMessage(Session.getInstance(mailProps,
					new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			}));
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
			
			message.setSubject(subject);
			message.setText(text);
			
			Transport.send(message);
			sent = true;
		}
		catch (MessagingException e) {
			System.out.println(e.getMessage());
			sent = false;
		}
		return sent;
	}
}