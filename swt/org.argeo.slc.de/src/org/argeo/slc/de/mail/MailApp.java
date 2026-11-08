package org.argeo.slc.de.mail;

import java.io.Console;
import java.security.GeneralSecurityException;
import java.util.Properties;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class MailApp {

	protected Store connectToSourceStore(String host, String port, String user, String password)
			throws MessagingException, GeneralSecurityException {
		Properties properties = new Properties();// System.getProperties();
		properties.setProperty("mail.imap.starttls.enable", "true");
//		properties.setProperty("mail.imap.starttls.required", "true");
		properties.setProperty("mail.imap.auth", "true");
//		properties.setProperty("mail.imap.auth.mechanisms", "LOGIN");
		properties.setProperty("mail.imap.ssl.checkserveridentity", "false");
		properties.setProperty("mail.imap.ssl.trust", "*");

		Session session = Session.getInstance(properties, null);
		session.setDebug(true);
		Store store = session.getStore("imap");
		store.connect(host, Integer.parseInt(port), user, password);
		return store;
	}

	public static void main(String[] args) throws Exception {
		MailApp mailApp = new MailApp();
		
		Console console = System.console();

		if (console == null) {
			System.err.println("Console is not available.");
			mailApp.connectToSourceStore(args[0], args[1], args[2], args[3]);
			System.exit(1);
		}

		char[] password = console.readPassword("Enter password: ");
		mailApp.connectToSourceStore(args[0], args[1], args[2], new String(password));
		// mailApp.connectToSourceStore(args[0], args[1], args[2], args[3]);
	}

}
