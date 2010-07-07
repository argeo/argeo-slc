package org.argeo.slc.detached;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;

/**
 * When started, processes <code>DetachedRequest</code> through a
 * <code>DetachedExecutionServer</code> and sends <code>DetachedAnswer</code>
 * back
 */
public class DetachedServer implements BundleContextAware, ApplicationContextAware {

	private final static Log log = LogFactory.getLog(DetachedServer.class);	
	
	private boolean active = true;
	private DetachedExecutionServer executionServer = null;

	private boolean cacheObjects = true;

	/** May be null */
	private ApplicationContext applicationContext;
	/** May be null */
	private BundleContext bundleContext;
	
	/**
	 * Used to receive Request and send answers
	 */
	private DetachedDriver detachedDriver;

	public synchronized void start() {

		log.info("Starting DetachedServer");
		
		Thread driverThread = new Thread(new Runnable() {

			public void run() {
				while (active) {
					try {
						// no timeout to receive a request
						DetachedRequest request = detachedDriver.receiveRequest();
						if (!active)
							break;

						String driverBundleName = null;
						if (bundleContext != null)
							driverBundleName = bundleContext.getBundle()
									.getSymbolicName();

						if (applicationContext != null && cacheObjects) {
							try {
								String ref = request.getRef();
								if (applicationContext.containsBean(ref)) {
									Object obj = applicationContext
											.getBean(request.getRef());
									request.setCachedObject(obj);
									if (log.isTraceEnabled())
										log.trace("Cached bean '" + ref
												+ "' in request " + request);
								} else {
									log
											.warn("Cannot cache object in request because no bean '"
													+ ref
													+ "' was found in application context"
													+ (driverBundleName != null ? " (driver bundle "
															+ driverBundleName
															+ ")"
															: ""));
								}
							} catch (Exception e) {
								if (log.isTraceEnabled())
									log
											.trace("Could not retrieve "
													+ request.getRef()
													+ " from driver application context because of "
													+ e);
								driverBundleName = null;// do not publish bundle
														// name
							}
						}

						if (driverBundleName != null)
							request.getProperties().put(
									Constants.BUNDLE_SYMBOLICNAME,
									driverBundleName);

						DetachedAnswer answer = executionServer
								.executeRequest(request);
						detachedDriver.sendAnswer(answer);
					} catch (Exception e) {
						// if (e instanceof RuntimeException)
						// throw (RuntimeException) e;
						// else
						e.printStackTrace();
					}
				}

			}
		}, "driverThread (" + getClass() + ")");
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

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void setCacheObjects(boolean cacheObjects) {
		this.cacheObjects = cacheObjects;
	}

	public void setDetachedDriver(DetachedDriver detachedDriver) {
		this.detachedDriver = detachedDriver;
	}	
	
}
