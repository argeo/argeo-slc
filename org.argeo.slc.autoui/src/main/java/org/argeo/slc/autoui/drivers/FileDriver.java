package org.argeo.slc.autoui.drivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.argeo.slc.autoui.DetachedClient;
import org.argeo.slc.autoui.DetachedDriver;
import org.argeo.slc.autoui.DetachedStepAnswer;
import org.argeo.slc.autoui.DetachedStepRequest;

public class FileDriver implements DetachedDriver, DetachedClient {
	private File requestDir;
	private File answerDir;

	public synchronized DetachedStepRequest receiveRequest() throws Exception {
		File file = null;
		while (file == null) {
			File[] files = requestDir.listFiles();
			if (files.length > 0)
				file = files[0];
			else
				Thread.sleep(1000);
		}

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		DetachedStepRequest request = (DetachedStepRequest) in.readObject();
		in.close();
		
		file.delete();// move it to a processed dir instead?
		return request;
	}

	public void sendAnswer(DetachedStepAnswer answer) throws Exception {
		// TODO Auto-generated method stub

	}

	public DetachedStepAnswer receiveAnswer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendRequest(DetachedStepRequest request) throws Exception {
		File file = new File(requestDir.getPath() + File.separator
				+ request.getUuid());
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				file));
		out.writeObject(request);
		out.close();
	}

	public void setRequestDir(File requestDir) {
		this.requestDir = requestDir;
	}

	public void setAnswerDir(File answerDir) {
		this.answerDir = answerDir;
	}

}
