package org.argeo.slc.equinox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.osgi.OsgiExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.springframework.beans.factory.InitializingBean;

public class ExecutionCommandProvider implements CommandProvider,
		InitializingBean {
	private final static Log log = LogFactory
			.getLog(ExecutionCommandProvider.class);

	private OsgiExecutionModulesManager modulesManager;

	private RealizedFlow lastLaunch = null;

	public Object _slc(CommandInterpreter ci) {
		// TODO: check version
		String firstArg = ci.nextArgument();
		if (firstArg == null) {
			if (lastLaunch != null) {
				String cmd = "slc " + lastLaunch.getModuleName() + " "
						+ lastLaunch.getFlowDescriptor().getName();
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
		return "COMMAND COMPLETED";
	}

	protected void launch(String firstArg, String executionName) {
		lastLaunch = modulesManager.findRealizedFlow(firstArg, executionName);
		if (lastLaunch == null)
			throw new SlcException("Cannot find launch for " + firstArg + " "
					+ executionName);

		modulesManager.updateAndExecute(lastLaunch);

	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf
				.append("\tslc (<id>|<segment of bsn>) <execution bean>  - execute an execution flow (without arg, execute last)\n");
		return buf.toString();

	}

	public void setModulesManager(OsgiExecutionModulesManager osgiModulesManager) {
		this.modulesManager = osgiModulesManager;
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
}
