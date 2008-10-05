package org.argeo.slc.detached.drivers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedCommunication;
import org.argeo.slc.detached.DetachedException;
import org.argeo.slc.detached.DetachedRequest;
import org.springframework.beans.factory.InitializingBean;

public class FileDriver extends AbstractDriver implements DetachedClient,
		InitializingBean {
	private final static Log log = LogFactory.getLog(FileDriver.class);

	private File baseDir;
	private File requestsDir;
	private File answersDir;
	private File processedRequestsDir;
	private File processedAnswersDir;

	private String lockFileExt = "lck";
	private FileFilter notLockFileFilter = new NotFileFilter(
			new SuffixFileFilter("." + lockFileExt));

	public synchronized DetachedRequest receiveRequest() throws Exception {
		DetachedRequest request = (DetachedRequest) receiveFile(requestsDir,
				processedRequestsDir);
		if (request != null)
			log.debug("Received detached request #" + request.getUuid()
					+ " for ref '" + request.getRef() + "', path="
					+ request.getPath());
		return request;
	}

	public void sendAnswer(DetachedAnswer answer) throws Exception {
		sendFile(answersDir, answer);
		log.debug("Sent     detached answer  #" + answer.getUuid());
	}

	public DetachedAnswer receiveAnswer() throws Exception {
		DetachedAnswer answer = (DetachedAnswer) receiveFile(answersDir,
				processedAnswersDir);
		if (answer != null)
			log.debug("Received detached answer  #" + answer.getUuid());
		return answer;
	}

	public void sendRequest(DetachedRequest request) throws Exception {
		sendFile(requestsDir, request);
		log.debug("Sent     detached request #" + request.getUuid()
				+ " for ref '" + request.getRef() + "', path="
				+ request.getPath());
	}

	protected void sendFile(File dir, DetachedCommunication detCom)
			throws Exception {
		final File file;
		if (getXmlConverter() != null)
			file = new File(dir.getPath() + File.separator + detCom.getUuid()
					+ ".xml");
		else
			file = new File(dir.getPath() + File.separator + detCom.getUuid());

		File lockFile = createLockFile(file);
		if (getXmlConverter() != null) {
			FileOutputStream outFile = new FileOutputStream(file);
			try {
				StreamResult result = new StreamResult(outFile);
				getXmlConverter().marshallCommunication(detCom, result);
			} finally {
				IOUtils.closeQuietly(outFile);
			}
		} else {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(file));
			try {
				out.writeObject(detCom);
			} finally {
				IOUtils.closeQuietly(out);
			}
		}
		lockFile.delete();
	}

	protected synchronized DetachedCommunication receiveFile(File dir,
			File processedDir) throws Exception {
		File file = null;
		while (file == null && isActive()) {
			if (!dir.exists())
				throw new DetachedException("Dir " + dir + " does not exist.");

			File[] files = dir.listFiles(notLockFileFilter);
			if (files.length > 0)
				file = files[0];
			else {
				try {
					wait(100);
				} catch (InterruptedException e) {
					// silent
				}
			}
		}

		if (!isActive())
			return null;

		File lockFile = nameLockFile(file);
		while (lockFile.exists())
			// FIXME: implements time out
			Thread.sleep(100);

		// Read the file
		final DetachedCommunication detCom;
		if (FilenameUtils.getExtension(file.getName()).equals("xml")) {
			if (getXmlConverter() == null)
				throw new DetachedException("No XML converter defined.");
			FileInputStream in = new FileInputStream(file);
			try {
				StreamSource source = new StreamSource(in);
				detCom = getXmlConverter().unmarshallCommunication(source);
			} finally {
				IOUtils.closeQuietly(in);
			}
		} else {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					file));
			try {
				detCom = (DetachedCommunication) in.readObject();
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		// Move to processed dir
		FileUtils.moveFileToDirectory(file, processedDir, false);
		// file.renameTo(new File(processedDir.getAbsolutePath() +
		// File.separator
		// + file.getName()));
		return detCom;
	}

	protected File createLockFile(File file) {
		File lockFile = nameLockFile(file);
		try {
			lockFile.createNewFile();
		} catch (IOException e) {
			throw new DetachedException("Cannot create lock file " + lockFile);
		}
		return lockFile;
	}

	protected File nameLockFile(File file) {
		return new File(file.getAbsolutePath() + "." + lockFileExt);
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	private void createIfNotExist(File dir) {
		if (!dir.exists()) {
			log.warn("Dir " + dir.getAbsolutePath()
					+ " does not exist. Creating it...");
			dir.mkdirs();
		}
	}

	public void afterPropertiesSet() throws Exception {
		this.requestsDir = new File(baseDir.getAbsolutePath() + File.separator
				+ "requests");
		this.answersDir = new File(baseDir.getAbsolutePath() + File.separator
				+ "answers");
		this.processedRequestsDir = new File(baseDir.getAbsolutePath()
				+ File.separator + "processed" + File.separator + "requests");
		this.processedAnswersDir = new File(baseDir.getAbsolutePath()
				+ File.separator + "processed" + File.separator + "answers");

		createIfNotExist(requestsDir);
		createIfNotExist(answersDir);
		createIfNotExist(processedRequestsDir);
		createIfNotExist(processedAnswersDir);
	}

}
