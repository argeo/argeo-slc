package org.argeo.slc.equinox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;

public class ExecutionCommandProvider implements CommandProvider,
		BundleContextAware, FrameworkListener, InitializingBean {
	private final static Log log = LogFactory
			.getLog(ExecutionCommandProvider.class);

	private Long defaultTimeout = 10000l;

	private BundleContext bundleContext;

	private String lastModuleName = null;
	private String lastExecutionName = null;

	private final Object refreshedPackageSem = new Object();

	/** @deprecated Use slc command instead. */
	public Object _slc_exec(CommandInterpreter ci) {
		return _slc(ci);
	}

	public Object _slc(CommandInterpreter ci) {
		// TODO: check version
		String firstArg = ci.nextArgument();
		if (firstArg == null) {
			if (lastModuleName != null) {
				String cmd = "slc " + lastModuleName + " " + lastExecutionName;
				if (log.isDebugEnabled())
					log.debug("Execute again last command: " + cmd);
				return ci.execute(cmd);
			} else {
				ci.execute("help");
				throw new SlcException("Command not properly formatted");
			}
		}
		String executionName = ci.nextArgument();

		String moduleName = null;

		// First check whether we have a bundleId
		Long bundleId = null;
		try {
			bundleId = Long.parseLong(firstArg);
		} catch (NumberFormatException e) {
			// silent
		}

		// Look for bundle names containing pattern
		Bundle bundle = null;
		if (bundleId != null) {
			bundle = bundleContext.getBundle(bundleId);
		} else {
			for (Bundle b : bundleContext.getBundles()) {
				if (b.getSymbolicName().contains(firstArg)) {
					bundle = b;
					break;
				}
			}
		}

		if (bundle != null) {
			moduleName = bundle.getSymbolicName();
			lastModuleName = moduleName;
			lastExecutionName = executionName;
		} else {
			log
					.warn("Could not find any execution module matching these requirements.");
			return null;
		}

		// Find module
		ExecutionModule module = null;
		ServiceReference serviceRef = null;
		try {
			stopSynchronous(bundle);
			updateSynchronous(bundle);
			// Refresh in case there are fragments
			refreshSynchronous(bundle);
			startSynchronous(bundle);

			String filter = "(Bundle-SymbolicName=" + moduleName + ")";
			// Wait for application context to be ready
			getServiceRefSynchronous(ApplicationContext.class.getName(), filter);

			if (log.isDebugEnabled())
				log.debug("Bundle " + bundle.getSymbolicName()
						+ " ready to be used at latest version.");

			ServiceReference[] sfs = getServiceRefSynchronous(
					ExecutionModule.class.getName(), filter);

			if (sfs.length > 1)
				log
						.warn("More than one execution module service found in module "
								+ moduleName);

			if (sfs.length > 0) {
				serviceRef = sfs[0];
				module = (ExecutionModule) bundleContext.getService(serviceRef);
			}

			if (module != null) {
				ExecutionFlowDescriptor descriptor = new ExecutionFlowDescriptor();
				descriptor.setName(executionName);
				module.execute(descriptor);
				log.info("Executed " + executionName + " from " + moduleName);
			}

		} catch (Exception e) {
			throw new SlcException("Cannot find or update module.", e);
		} finally {
			if (serviceRef != null)
				bundleContext.ungetService(serviceRef);
		}

		return "COMMAND COMPLETED";
	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf
				.append("\tslc (<id>|<segment of bsn>) <execution bean>  - execute an execution flow (without arg, execute last)\n");
		return buf.toString();

	}

	/** Updates bundle synchronously. */
	protected void updateSynchronous(Bundle bundle) throws BundleException {
		// int originalState = bundle.getState();
		bundle.update();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			int state = bundle.getState();
			if (state == Bundle.INSTALLED || state == Bundle.ACTIVE
					|| state == Bundle.RESOLVED)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Update of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " updated.");
	}

	/** Starts bundle synchronously. Does nothing if already started. */
	protected void startSynchronous(Bundle bundle) throws BundleException {
		int originalState = bundle.getState();
		if (originalState == Bundle.ACTIVE)
			return;

		bundle.start();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			if (bundle.getState() == Bundle.ACTIVE)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Start of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " started.");
	}

	/** Stops bundle synchronously. Does nothing if already started. */
	protected void stopSynchronous(Bundle bundle) throws BundleException {
		int originalState = bundle.getState();
		if (originalState != Bundle.ACTIVE)
			return;

		bundle.stop();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			if (bundle.getState() != Bundle.ACTIVE
					&& bundle.getState() != Bundle.STOPPING)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Stop of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " stopped.");
	}

	/** Refresh bundle synchronously. Does nothing if already started. */
	protected void refreshSynchronous(Bundle bundle) throws BundleException {
		ServiceReference packageAdminRef = bundleContext
				.getServiceReference(PackageAdmin.class.getName());
		PackageAdmin packageAdmin = (PackageAdmin) bundleContext
				.getService(packageAdminRef);
		Bundle[] bundles = { bundle };
		packageAdmin.refreshPackages(bundles);

		synchronized (refreshedPackageSem) {
			try {
				refreshedPackageSem.wait(defaultTimeout);
			} catch (InterruptedException e) {
				// silent
			}
		}

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " refreshed.");
	}

	public void frameworkEvent(FrameworkEvent event) {
		if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
			synchronized (refreshedPackageSem) {
				refreshedPackageSem.notifyAll();
			}
		}
	}

	protected ServiceReference[] getServiceRefSynchronous(String clss,
			String filter) throws InvalidSyntaxException {
		if (log.isTraceEnabled())
			log.debug("Filter: '" + filter + "'");
		ServiceReference[] sfs = null;
		boolean waiting = true;
		long begin = System.currentTimeMillis();
		do {
			sfs = bundleContext.getServiceReferences(clss, filter);

			if (sfs != null)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Search of services " + clss
						+ " with filter " + filter + " timed out.");
		} while (waiting);

		return sfs;
	}

	protected void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// silent
		}
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void afterPropertiesSet() throws Exception {
		bundleContext.addFrameworkListener(this);
	}

	public void setDefaultTimeout(Long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

}
