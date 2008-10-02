package org.argeo.slc.detached.drivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
		File file = new File(dir.getPath() + File.separator + detCom.getUuid());
		File lockFile = createLockFile(file);
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				file));
		out.writeObject(detCom);
		out.close();
		lockFile.delete();
	}

	protected synchronized DetachedCommunication receiveFile(File dir,
			File processedDir) throws Exception {
		File file = null;
		while (file == null && isActive()) {
			if (!dir.exists())
				throw new DetachedException("Dir " + dir + " does not exist.");

			File[] files = dir.listFiles();
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
			// TODO: implements time out
			Thread.sleep(100);

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		DetachedCommunication detCom = (DetachedCommunication) in.readObject();
		in.close();

		// Move to processed dir
		file.renameTo(new File(processedDir.getAbsolutePath() + File.separator
				+ file.getName()));
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
		return new File(file.getAbsolutePath() + ".lck");
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
