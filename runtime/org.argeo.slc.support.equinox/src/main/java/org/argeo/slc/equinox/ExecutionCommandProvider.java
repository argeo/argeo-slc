package org.argeo.slc.equinox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

public class ExecutionCommandProvider implements CommandProvider,
		BundleContextAware {
	private final static Log log = LogFactory
			.getLog(ExecutionCommandProvider.class);

	private List<ExecutionModule> executionModules;
	private BundleContext bundleContext;

	public Object _slc_exec(CommandInterpreter ci) {
		// TODO: check version
		String firstArg = ci.nextArgument();
		String executionName = ci.nextArgument();
		String moduleName = null;

		Long bundleId = null;
		try {
			bundleId = Long.parseLong(firstArg);
		} catch (NumberFormatException e) {
			// silent
		}

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
			// try {
			// bundle.stop();
			// bundle.update();
			// bundle.start();
			//
			// // FIXME: potential infinite loop
			// while (bundle.getState() == Bundle.STARTING) {
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// // silent
			// }
			// }
			// } catch (BundleException e) {
			// throw new SlcException(
			// "Could not update the bundle for module " + moduleName,
			// e);
			// }
		}

		// Find module
		ExecutionModule module = null;
		if (moduleName != null) {
			for (Iterator<ExecutionModule> it = executionModules.iterator(); it
					.hasNext();) {
				ExecutionModule moduleT = it.next();
				if (moduleT.getName().equals(moduleName)) {
					module = moduleT;
					break;
				}
			}
		}

		if (module != null) {
			ExecutionFlowDescriptor descriptor = new ExecutionFlowDescriptor();
			descriptor.setName(executionName);
			module.execute(descriptor);
			log.info("Executed " + executionName + " from " + moduleName);
		} else
			log
					.warn("Could not find any execution module matching these requirements.");

		return null;
	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf
				.append("\tslc_exec (<id>|<segment of bsn>) <execution bean>  - execute an execution flow\n");
		return buf.toString();

	}

	public void setExecutionModules(List<ExecutionModule> executionModules) {
		this.executionModules = executionModules;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
