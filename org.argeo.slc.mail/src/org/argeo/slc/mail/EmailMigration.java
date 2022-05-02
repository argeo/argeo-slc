package org.argeo.slc.mail;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import static org.argeo.slc.mail.EmailUtils.describe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.HeaderTerm;
import javax.mail.util.SharedFileInputStream;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.mbox.MboxFolder;
import com.sun.mail.mbox.MboxMessage;

/** Migrates emails from one storage to the another one. */
public class EmailMigration {
	private final static Logger logger = System.getLogger(EmailMigration.class.getName());

	private String targetBaseDir;
	private String sourceServer;
	private String sourceUsername;
	private String sourcePassword;

	public void process() throws MessagingException, IOException {
		Path baseDir = Paths.get(targetBaseDir).resolve(sourceUsername).resolve("mbox");

		Store sourceStore = null;
		try {
			Properties sourceProperties = System.getProperties();
			sourceProperties.setProperty("mail.store.protocol", "imaps");

			Session sourceSession = Session.getDefaultInstance(sourceProperties, null);
			// session.setDebug(true);
			sourceStore = sourceSession.getStore("imaps");
			sourceStore.connect(sourceServer, sourceUsername, sourcePassword);

			Folder defaultFolder = sourceStore.getDefaultFolder();
			migrateFolders(baseDir, defaultFolder);

			// Always start with Inbox
//			Folder inboxFolder = sourceStore.getFolder(EmailUtils.INBOX);
//			migrateFolder(baseDir, inboxFolder);
		} finally {
			if (sourceStore != null)
				sourceStore.close();

		}
	}

	protected void migrateFolders(Path baseDir, Folder sourceFolder) throws MessagingException, IOException {
		folders: for (Folder folder : sourceFolder.list()) {
			String folderName = folder.getName();

			if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				// Make it configurable
				switch (folderName) {
				case "All Mail":
				case "Important":
					continue folders;
				default:
					// doe nothing
				}
				migrateFolder(baseDir, folder);
			}
			if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
				migrateFolders(baseDir.resolve(folder.getName()), folder);
			}
		}
	}

	protected void migrateFolder(Path baseDir, Folder sourceFolder) throws MessagingException, IOException {

		String folderName = sourceFolder.getName();
		sourceFolder.open(Folder.READ_ONLY);

		Folder targetFolder = null;
		try {
			int messageCount = sourceFolder.getMessageCount();
			logger.log(DEBUG, folderName + " - Message count : " + messageCount);
			if (messageCount == 0)
				return;
//			logger.log(DEBUG, folderName + " - Unread Messages : " + sourceFolder.getUnreadMessageCount());

			boolean saveAsFiles = false;

			if (saveAsFiles) {
				Message messages[] = sourceFolder.getMessages();

				for (int i = 0; i < messages.length; ++i) {
//					logger.log(DEBUG, "MESSAGE #" + (i + 1) + ":");
					Message msg = messages[i];
//					String from = "unknown";
//					if (msg.getReplyTo().length >= 1) {
//						from = msg.getReplyTo()[0].toString();
//					} else if (msg.getFrom().length >= 1) {
//						from = msg.getFrom()[0].toString();
//					}
					String subject = msg.getSubject();
					Instant sentDate = msg.getSentDate().toInstant();
//					logger.log(DEBUG, "Saving ... " + subject + " from " + from + " (" + sentDate + ")");
					String fileName = sentDate + "  " + subject;
					Path file = baseDir.resolve(fileName);
					savePartsAsFiles(msg.getContent(), file);
				}
			} else {
				long begin = System.currentTimeMillis();
				targetFolder = migrateFolderToMbox(baseDir, sourceFolder);
				long duration = System.currentTimeMillis() - begin;
				logger.log(DEBUG, folderName + " - Migration of " + messageCount + " messages took " + (duration / 1000)
						+ " s (" + (duration / messageCount) + " ms per message)");
			}
		} finally {
			sourceFolder.close();
			if (targetFolder != null)
				targetFolder.close();
		}
	}

	protected Folder migrateFolderToMbox(Path baseDir, Folder sourceFolder) throws MessagingException, IOException {
		String folderName = sourceFolder.getName();
		if (sourceFolder.getName().equals(EmailUtils.INBOX_UPPER_CASE))
			folderName = EmailUtils.INBOX;// Inbox

		Path targetDir = baseDir;// .resolve("mbox");
		Files.createDirectories(targetDir);
		Path targetPath;
		if (((sourceFolder.getType() & Folder.HOLDS_FOLDERS) != 0) && sourceFolder.list().length != 0) {
			Path dir = targetDir.resolve(folderName);
			Files.createDirectories(dir);
			targetPath = dir.resolve("_Misc");
		} else {
			targetPath = targetDir.resolve(folderName);
		}
		if (!Files.exists(targetPath))
			Files.createFile(targetPath);
		URLName targetUrlName = new URLName("mbox:" + targetPath.toString());
		Properties targetProperties = new Properties();
		// targetProperties.setProperty("mail.mime.address.strict", "false");
		Session targetSession = Session.getDefaultInstance(targetProperties);
		Folder targetFolder = targetSession.getFolder(targetUrlName);
		targetFolder.open(Folder.READ_WRITE);

		int lastSourceNumber;
		int currentTargetMessageCount = targetFolder.getMessageCount();
		if (currentTargetMessageCount != 0) {
			MimeMessage lastTargetMessage = (MimeMessage) targetFolder.getMessage(currentTargetMessageCount);
			logger.log(DEBUG, folderName + " - Last target message " + describe(lastTargetMessage));
			Date lastTargetSent = lastTargetMessage.getReceivedDate();
			Message[] lastSourceMessage = sourceFolder
					.search(new HeaderTerm(EmailUtils.MESSAGE_ID, lastTargetMessage.getMessageID()));
			if (lastSourceMessage.length == 0)
				throw new IllegalStateException("No message found with message ID " + lastTargetMessage.getMessageID());
			if (lastSourceMessage.length != 1) {
				for (Message msg : lastSourceMessage) {
					logger.log(ERROR, "Message " + describe(msg));

				}
				throw new IllegalStateException(
						lastSourceMessage.length + " messages found with received date " + lastTargetSent.toInstant());
			}
			lastSourceNumber = lastSourceMessage[0].getMessageNumber();
		} else {
			lastSourceNumber = 0;
		}
		logger.log(DEBUG, folderName + " - Last source message number " + lastSourceNumber);

		int countToRetrieve = sourceFolder.getMessageCount() - lastSourceNumber;
//	for (int i = startNumber; i < messageCount; i++) {
//		long begin = System.currentTimeMillis();
//		Message message = sourceFolder.getMessage(i);
//		targetFolder.appendMessages(new Message[] { message });
//		long duration = System.currentTimeMillis() - begin;
//		logger.log(DEBUG, "Message " + i + " migrated in " + duration + " ms");
//	}

		FetchProfile fetchProfile = new FetchProfile();
		fetchProfile.add(FetchProfile.Item.FLAGS);
		fetchProfile.add(FetchProfile.Item.ENVELOPE);
		fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
		fetchProfile.add(FetchProfile.Item.SIZE);
		if (sourceFolder instanceof IMAPFolder) {
			// IMAPFolder sourceImapFolder = (IMAPFolder) sourceFolder;
			fetchProfile.add(IMAPFolder.FetchProfileItem.HEADERS);
			fetchProfile.add(IMAPFolder.FetchProfileItem.MESSAGE);
		}

		int batchSize = 100;
		int batchCount = countToRetrieve / batchSize;
		if (countToRetrieve % batchSize != 0)
			batchCount = batchCount + 1;
		// int batchCount = 2; // for testing
		for (int i = 0; i < batchCount; i++) {
			long begin = System.currentTimeMillis();

			int start = lastSourceNumber + i * batchSize + 1;
			int end = lastSourceNumber + (i + 1) * batchSize;
			if (end >= (lastSourceNumber + countToRetrieve + 1))
				end = lastSourceNumber + countToRetrieve;
			Message[] sourceMessages = sourceFolder.getMessages(start, end);
			sourceFolder.fetch(sourceMessages, fetchProfile);
			// targetFolder.appendMessages(sourceMessages);
			// sourceFolder.copyMessages(sourceMessages,targetFolder);

			Message[] targetMessages = new Message[sourceMessages.length];
			for (int j = 0; j < sourceMessages.length; j++) {
				MimeMessage sourceMm = (MimeMessage) sourceMessages[j];
				InternetHeaders ih = new InternetHeaders();
				for (Enumeration<String> e = sourceMm.getAllHeaderLines(); e.hasMoreElements();) {
					ih.addHeaderLine(e.nextElement());
				}
//			Flags flags = sourceMm.getFlags();
//			StringBuilder status = new StringBuilder();
//			if (flags.contains(Flags.Flag.SEEN))
//				status.append('R');
//			if (!flags.contains(Flags.Flag.RECENT))
//				status.append('O');
//			if (status.length() > 0 && ih.getHeader("X-Status") == null)
//				ih.setHeader("X-Status", status.toString());

				Path tmpFileSource = Files.createTempFile("argeo-mbox-source", ".txt");
				Path tmpFileTarget = Files.createTempFile("argeo-mbox-target", ".txt");
				// logger.log(DEBUG, "tmpFileSource " + tmpFileSource + ", tmpFileTarget " +
				// tmpFileTarget);
				Files.copy(sourceMm.getRawInputStream(), tmpFileSource, StandardCopyOption.REPLACE_EXISTING);

				// we use ISO_8859_1 because it is more robust than US_ASCII with regard to
				// missing characters
				try (BufferedReader reader = Files.newBufferedReader(tmpFileSource, StandardCharsets.ISO_8859_1);
						BufferedWriter writer = Files.newBufferedWriter(tmpFileTarget, StandardCharsets.ISO_8859_1);) {
					int lineNumber = 0;
					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							lineNumber++;
							if (line.startsWith("From ")) {
								writer.write(">" + line);
								logger.log(DEBUG, "Fix line " + lineNumber + " in " + EmailUtils.describe(sourceMm)
										+ ": " + line);
							} else {
								writer.write(line);
							}
							writer.newLine();
						}
					} catch (IOException e) {
						logger.log(ERROR, "Error around line " + lineNumber + " of " + tmpFileSource);
						throw e;
					}
				}

				MboxMessage mboxMessage = new MboxMessage((MboxFolder) targetFolder, ih,
						new SharedFileInputStream(tmpFileTarget.toFile()), sourceMm.getMessageNumber(),
						EmailUtils.getUnixFrom(sourceMm), true);
				targetMessages[j] = mboxMessage;

				// clean up
				Files.delete(tmpFileSource);
				Files.delete(tmpFileTarget);
			}
			targetFolder.appendMessages(targetMessages);
//		Message[] targetMessages = targetFolder.getMessages(start, end);
//		for (int j = 0; j < sourceMessages.length; j++) {
//			EmailUtils.setHeadersFromFlags((MimeMessage) targetMessages[j], sourceMessages[j].getFlags());
////			Flags flags = sourceMessages[j].getFlags();
////			targetMessages[j].setFlags(flags, true);
//			targetMessages[j].saveChanges();
//		}

			String describeLast = describe(sourceMessages[sourceMessages.length - 1]);

//		if (i % 10 == 9) {
			// free memory from fetched messages
			sourceFolder.close();
			targetFolder.close();

			sourceFolder.open(Folder.READ_ONLY);
			targetFolder.open(Folder.READ_WRITE);
//			logger.log(DEBUG, "Open/close folder in order to free memory");
//		}

			long duration = System.currentTimeMillis() - begin;
			logger.log(DEBUG, folderName + " - batch " + i + " took " + (duration / 1000) + " s, "
					+ (duration / (end - start + 1)) + " ms per message. Last message " + describeLast);
		}

		return targetFolder;
	}

	/** Save body parts and attachments as plain files. */
	protected void savePartsAsFiles(Object content, Path fileBase) throws IOException, MessagingException {
		OutputStream out = null;
		InputStream in = null;
		try {
			if (content instanceof Multipart) {
				Multipart multi = ((Multipart) content);
				int parts = multi.getCount();
				for (int j = 0; j < parts; ++j) {
					MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(j);
					if (part.getContent() instanceof Multipart) {
						// part-within-a-part, do some recursion...
						savePartsAsFiles(part.getContent(), fileBase);
					} else {
						String extension = "";
						if (part.isMimeType("text/html")) {
							extension = "html";
						} else {
							if (part.isMimeType("text/plain")) {
								extension = "txt";
							} else {
								// Try to get the name of the attachment
								extension = part.getDataHandler().getName();
							}
						}
						String filename = fileBase + "." + extension;
						System.out.println("... " + filename);
						out = new FileOutputStream(new File(filename));
						in = part.getInputStream();
						int k;
						while ((k = in.read()) != -1) {
							out.write(k);
						}
					}
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	public void setTargetBaseDir(String targetBaseDir) {
		this.targetBaseDir = targetBaseDir;
	}

	public void setSourceServer(String sourceServer) {
		this.sourceServer = sourceServer;
	}

	public void setSourceUsername(String sourceUsername) {
		this.sourceUsername = sourceUsername;
	}

	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	public static void main(String args[]) throws Exception {
		if (args.length < 4)
			throw new IllegalArgumentException(
					"usage: <target base dir> <source IMAP server> <source username> <source password>");
		String targetBaseDir = args[0];
		String sourceServer = args[1];
		String sourceUsername = args[2];
		String sourcePassword = args[3];

		EmailMigration emailMigration = new EmailMigration();
		emailMigration.setTargetBaseDir(targetBaseDir);
		emailMigration.setSourceServer(sourceServer);
		emailMigration.setSourceUsername(sourceUsername);
		emailMigration.setSourcePassword(sourcePassword);

		emailMigration.process();
	}
}
