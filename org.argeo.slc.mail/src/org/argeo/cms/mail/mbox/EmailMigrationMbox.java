package org.argeo.cms.mail.mbox;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Properties;

import org.argeo.cms.mail.EmailMigration;
import org.argeo.cms.mail.EmailUtils;
import org.eclipse.angus.mail.mbox.MboxFolder;
import org.eclipse.angus.mail.mbox.MboxMessage;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.URLName;
import jakarta.mail.internet.InternetHeaders;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.SharedFileInputStream;

public class EmailMigrationMbox extends EmailMigration {
	private final static Logger logger = System.getLogger(EmailMigrationMbox.class.getName());

	@Override
	protected void copyMessages(Message[] sourceMessages, Folder targetFolder) throws MessagingException, IOException {
		Message[] targetMessages = new Message[sourceMessages.length];
		for (int j = 0; j < sourceMessages.length; j++) {
			MimeMessage sourceMm = (MimeMessage) sourceMessages[j];
			InternetHeaders ih = new InternetHeaders();
			for (Enumeration<String> e = sourceMm.getAllHeaderLines(); e.hasMoreElements();) {
				ih.addHeaderLine(e.nextElement());
			}
			Path tmpFileSource = Files.createTempFile("argeo-mbox-source", ".txt");
			Path tmpFileTarget = Files.createTempFile("argeo-mbox-target", ".txt");
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
							logger.log(DEBUG,
									"Fix line " + lineNumber + " in " + EmailUtils.describe(sourceMm) + ": " + line);
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

	}

	@Override
	protected void migrateFolderToFs(Path baseDir, Folder sourceFolder) throws MessagingException, IOException {
		Folder targetFolder = null;
		try {
			targetFolder = openMboxTargetFolder(sourceFolder, baseDir);
			migrateFolder(sourceFolder, targetFolder);
		} finally {
			if (targetFolder != null)
				targetFolder.close();
		}
	}

	protected Folder openMboxTargetFolder(Folder sourceFolder, Path baseDir) throws MessagingException, IOException {
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

		return targetFolder;
	}

}
