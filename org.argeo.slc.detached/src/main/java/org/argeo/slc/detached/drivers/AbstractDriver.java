package org.argeo.slc.detached.drivers;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedDriver;
import org.argeo.slc.detached.DetachedExecutionServer;
import org.argeo.slc.detached.DetachedRequest;

public abstract class AbstractDriver implements DetachedDriver {
	private boolean active = true;
	private DetachedExecutionServer executionServer = null;

	public synchronized void start() {

		Thread driverThread = new Thread(new Runnable() {

			public void run() {
				while (active) {
					try {
						DetachedRequest request = receiveRequest();
						if (!active)
							break;
						DetachedAnswer answer = executionServer
								.executeStep(request);
						sendAnswer(answer);
					} catch (Exception e) {
						if (e instanceof RuntimeException)
							throw (RuntimeException) e;
						else
							e.printStackTrace();
					}
				}

			}
		}, "driverThread");
		driverThread.start();

	}

	public void setExecutionServer(DetachedExecutionServer executionServer) {
		this.executionServer = executionServer;
	}

	public synchronized void stop() {
		active = false;
		notifyAll();
	}

	public synchronized boolean isActive() {
		return active;
	}

	public synchronized void setActive(boolean active) {
		this.active = active;
	}

}
