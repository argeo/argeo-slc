package org.argeo.slc.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;

public class OsgiExecutionLauncher implements BundleContextAware {
	private final static Log log = LogFactory
			.getLog(OsgiExecutionLauncher.class);

	private BundleContext bundleContext;
	private BundlesManager bundlesManager;

	public Launch findLaunch(String firstArg, String executionName) {
		Launch launch = new Launch();

		// String moduleName = null;

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
			launch.setBundleId(bundle.getBundleId());
			launch.setModuleName(bundle.getSymbolicName());
			launch.setExecutionName(executionName);
			return launch;
		} else {
			log
					.warn("Could not find any execution module matching these requirements.");
			return null;
		}

	}

	public void launch(Launch launch) {
		Bundle bundle = bundleContext.getBundle(launch.getBundleId());

		// Find module
		ExecutionModule module = null;
		ServiceReference serviceRef = null;
		try {
			bundlesManager.stopSynchronous(bundle);
			bundlesManager.updateSynchronous(bundle);
			// Refresh in case there are fragments
			bundlesManager.refreshSynchronous(bundle);
			bundlesManager.startSynchronous(bundle);

			String filter = "(Bundle-SymbolicName=" + launch.getModuleName()
					+ ")";
			// Wait for application context to be ready
			bundlesManager.getServiceRefSynchronous(ApplicationContext.class
					.getName(), filter);

			if (log.isDebugEnabled())
				log.debug("Bundle " + bundle.getSymbolicName()
						+ " ready to be used at latest version.");

			ServiceReference[] sfs = bundlesManager.getServiceRefSynchronous(
					ExecutionModule.class.getName(), filter);

			if (sfs.length > 1)
				log
						.warn("More than one execution module service found in module "
								+ launch.getModuleName());

			if (sfs.length > 0) {
				serviceRef = sfs[0];
				module = (ExecutionModule) bundleContext.getService(serviceRef);
			}

			if (module != null) {
				ExecutionFlowDescriptor descriptor = new ExecutionFlowDescriptor();
				descriptor.setName(launch.getExecutionName());
				module.execute(descriptor);
				log.info("Executed " + launch.getExecutionName() + " from "
						+ launch.getExecutionName());
			}

		} catch (Exception e) {
			throw new SlcException("Cannot launch " + launch, e);
		} finally {
			if (serviceRef != null)
				bundleContext.ungetService(serviceRef);
		}
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void setBundlesManager(BundlesManager bundlesManager) {
		this.bundlesManager = bundlesManager;
	}

	public static class Launch {
		private Long bundleId;
		private String moduleName;
		private String executionName;

		public Long getBundleId() {
			return bundleId;
		}

		public void setBundleId(Long bundleId) {
			this.bundleId = bundleId;
		}

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

		public String getExecutionName() {
			return executionName;
		}

		public void setExecutionName(String executionName) {
			this.executionName = executionName;
		}

		@Override
		public String toString() {
			return moduleName + " " + executionName;
		}

	}
}
