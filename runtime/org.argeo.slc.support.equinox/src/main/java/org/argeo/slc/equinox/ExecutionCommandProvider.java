package org.argeo.slc.equinox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.osgi.BundlesManager;
import org.argeo.slc.osgi.OsgiExecutionLauncher;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.osgi.framework.launcher.Launcher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;

public class ExecutionCommandProvider implements CommandProvider,
		InitializingBean {
	private final static Log log = LogFactory
			.getLog(ExecutionCommandProvider.class);

	// private BundleContext bundleContext;
	// private BundlesManager bundlesManager;

	private OsgiExecutionLauncher executionLauncher;

	private OsgiExecutionLauncher.Launch lastLaunch = null;

	// private String lastModuleName = null;
	// private String lastExecutionName = null;

	/** @deprecated Use slc command instead. */
	public Object _slc_exec(CommandInterpreter ci) {
		return _slc(ci);
	}

	public Object _slc(CommandInterpreter ci) {
		// TODO: check version
		String firstArg = ci.nextArgument();
		if (firstArg == null) {
			if (lastLaunch != null) {
				String cmd = "slc " + lastLaunch.getModuleName() + " "
						+ lastLaunch.getExecutionName();
				if (log.isDebugEnabled())
					log.debug("Execute again last command: " + cmd);
				return ci.execute(cmd);
			} else {
				ci.execute("help");
				throw new SlcException("Command not properly formatted");
			}
		}
		String executionName = ci.nextArgument();

		launch(firstArg, executionName);
		/*
		 * String moduleName = null;
		 * 
		 * // First check whether we have a bundleId Long bundleId = null; try {
		 * bundleId = Long.parseLong(firstArg); } catch (NumberFormatException
		 * e) { // silent }
		 * 
		 * // Look for bundle names containing pattern Bundle bundle = null; if
		 * (bundleId != null) { bundle = bundleContext.getBundle(bundleId); }
		 * else { for (Bundle b : bundleContext.getBundles()) { if
		 * (b.getSymbolicName().contains(firstArg)) { bundle = b; break; } } }
		 * 
		 * if (bundle != null) { moduleName = bundle.getSymbolicName();
		 * lastModuleName = moduleName; lastExecutionName = executionName; }
		 * else { log
		 * .warn("Could not find any execution module matching these requirements."
		 * ); return null; }
		 * 
		 * // Find module ExecutionModule module = null; ServiceReference
		 * serviceRef = null; try { bundlesManager.stopSynchronous(bundle);
		 * bundlesManager.updateSynchronous(bundle); // Refresh in case there
		 * are fragments bundlesManager.refreshSynchronous(bundle);
		 * bundlesManager.startSynchronous(bundle);
		 * 
		 * String filter = "(Bundle-SymbolicName=" + moduleName + ")"; // Wait
		 * for application context to be ready
		 * bundlesManager.getServiceRefSynchronous(ApplicationContext.class
		 * .getName(), filter);
		 * 
		 * if (log.isDebugEnabled()) log.debug("Bundle " +
		 * bundle.getSymbolicName() + " ready to be used at latest version.");
		 * 
		 * ServiceReference[] sfs = bundlesManager.getServiceRefSynchronous(
		 * ExecutionModule.class.getName(), filter);
		 * 
		 * if (sfs.length > 1) log
		 * .warn("More than one execution module service found in module " +
		 * moduleName);
		 * 
		 * if (sfs.length > 0) { serviceRef = sfs[0]; module = (ExecutionModule)
		 * bundleContext.getService(serviceRef); }
		 * 
		 * if (module != null) { ExecutionFlowDescriptor descriptor = new
		 * ExecutionFlowDescriptor(); descriptor.setName(executionName);
		 * module.execute(descriptor); log.info("Executed " + executionName +
		 * " from " + moduleName); }
		 * 
		 * } catch (Exception e) { throw new
		 * SlcException("Cannot find or update module.", e); } finally { if
		 * (serviceRef != null) bundleContext.ungetService(serviceRef); }
		 */

		return "COMMAND COMPLETED";
	}

	protected void launch(String firstArg, String executionName) {
		lastLaunch = executionLauncher.findLaunch(firstArg, executionName);
		if (lastLaunch == null)
			throw new SlcException("Cannot find launch for " + firstArg + " "
					+ executionName);

		executionLauncher.launch(lastLaunch);

	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf
				.append("\tslc (<id>|<segment of bsn>) <execution bean>  - execute an execution flow (without arg, execute last)\n");
		return buf.toString();

	}

	public void setExecutionLauncher(OsgiExecutionLauncher launcher) {
		this.executionLauncher = launcher;
	}

	public void afterPropertiesSet() throws Exception {
		final String module = System.getProperty("slc.launch.module");
		final String executionName = System.getProperty("slc.launch.execution");
		if (module != null) {
			new Thread() {

				@Override
				public void run() {
					try {
						launch(module, executionName);
						// in case of failure OSGi runtime stays up and last
						// launch can be used to debug by calling 'slc'
					} catch (Exception e) {
						throw new SlcException("Error when executing "
								+ executionName + " on " + module, e);
					}
					try {
						EclipseStarter.shutdown();
					} catch (Exception e) {
						throw new SlcException("Cannot shutdown equinox.", e);
					}
				}

			}.start();
		}

	}

	// public void setBundleContext(BundleContext bundleContext) {
	// this.bundleContext = bundleContext;
	// }

	// public void setBundlesManager(BundlesManager bundlesManager) {
	// this.bundlesManager = bundlesManager;
	// }

}
