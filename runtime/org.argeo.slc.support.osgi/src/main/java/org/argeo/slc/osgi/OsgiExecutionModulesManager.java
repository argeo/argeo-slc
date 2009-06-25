package org.argeo.slc.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.AbstractExecutionModulesManager;
import org.argeo.slc.core.execution.DefaultDescriptorConverter;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.process.RealizedFlow;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class OsgiExecutionModulesManager extends AbstractExecutionModulesManager implements
		InitializingBean, DisposableBean {
	private final static Log log = LogFactory.getLog(OsgiExecutionModulesManager.class);

	private BundlesManager bundlesManager;
	private ServiceTracker executionContexts;
	private ExecutionFlowDescriptorConverter defaultDescriptorConverter = new DefaultDescriptorConverter();

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		return createDescriptor(moduleName, version, listFlows(moduleName,
				version));
	}

	public List<ExecutionModuleDescriptor> listExecutionModules() {
		List<ExecutionModuleDescriptor> descriptors = new ArrayList<ExecutionModuleDescriptor>();

		ServiceReference[] srs = executionContexts.getServiceReferences();
		for (ServiceReference sr : srs) {
			String moduleName = sr.getBundle().getSymbolicName();
			String moduleVersion = sr.getBundle().getHeaders().get(
					"Bundle-Version").toString();
			ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
			md.setName(moduleName);
			md.setVersion(moduleVersion);
			descriptors.add(md);
		}
		return descriptors;
	}

	protected Map<String, ExecutionFlow> listFlows(String moduleName,
			String moduleVersion) {
		// TODO: use service trackers?
		// String filter = OsgiFilterUtils.unifyFilter(ExecutionFlow.class,
		// null);

		String filter = "(Bundle-SymbolicName=" + moduleName + ")";
		ServiceReference[] sfs;
		try {
			sfs = bundlesManager.getBundleContext().getServiceReferences(
					ExecutionFlow.class.getName(), filter);
		} catch (InvalidSyntaxException e) {
			throw new SlcException(
					"Cannot retrieve service reference for flow " + filter, e);
		}

		Map<String, ExecutionFlow> flows = new HashMap<String, ExecutionFlow>();
		for (ServiceReference sf : sfs) {
			ExecutionFlow flow = (ExecutionFlow) bundlesManager
					.getBundleContext().getService(sf);
			flows.put(flow.getName(), flow);
		}
		return flows;
	}

	public ExecutionFlow findExecutionFlow(String moduleName,
			String moduleVersion, String flowName) {
		String filter = "(&(Bundle-SymbolicName=" + moduleName
				+ ")(org.springframework.osgi.bean.name=" + flowName + "))";
		return bundlesManager.getSingleServiceStrict(ExecutionFlow.class,
				filter);
	}

	public ExecutionContext findExecutionContext(String moduleName,
			String moduleVersion) {
		String filter = "(&(Bundle-SymbolicName=" + moduleName
				+ ")(Bundle-Version=" + moduleVersion + "))";
		return bundlesManager.getSingleServiceStrict(ExecutionContext.class,
				filter);
	}

	public ExecutionFlowDescriptorConverter findExecutionFlowDescriptorConverter(
			String moduleName, String moduleVersion) {
		String filter = "(&(Bundle-SymbolicName=" + moduleName
				+ ")(Bundle-Version=" + moduleVersion + "))";
		return bundlesManager.getSingleService(
				ExecutionFlowDescriptorConverter.class, filter);
	}

	public void setBundlesManager(BundlesManager bundlesManager) {
		this.bundlesManager = bundlesManager;
	}

	public void afterPropertiesSet() throws Exception {
		executionContexts = bundlesManager.newTracker(ExecutionContext.class);
	}

	public void destroy() throws Exception {
		if (executionContexts != null)
			executionContexts.close();
	}

	/**
	 * Builds a minimal realized flow, based on the provided information
	 * (typically from the command line).
	 * 
	 * @param module
	 *            a bundle id, or a pattern contained in a bundle symbolic name
	 * @param module
	 *            the execution flow name
	 * @return a minimal realized flow, to be used in an execution
	 */
	public RealizedFlow findRealizedFlow(String module, String executionName) {
		// First check whether we have a bundleId
		Long bundleId = null;
		try {
			bundleId = Long.parseLong(module);
		} catch (NumberFormatException e) {
			// silent
		}

		// Look for bundle names containing pattern
		OsgiBundle bundle = null;
		if (bundleId != null) {
			bundle = bundlesManager.getBundle(bundleId);
		} else {
			bundle = bundlesManager.findFromPattern(module);
		}

		if (bundle != null) {
			RealizedFlow launch = new RealizedFlow();
			launch.setModuleName(bundle.getName());
			launch.setModuleVersion(bundle.getVersion());
			ExecutionFlowDescriptor descriptor = new ExecutionFlowDescriptor();
			descriptor.setName(executionName);
			launch.setFlowDescriptor(descriptor);
			return launch;
		} else {
			log
					.warn("Could not find any execution module matching these requirements.");
			return null;
		}
	}

	public void updateAndExecute(RealizedFlow realizedFlow) {
		OsgiBundle osgiBundle = new OsgiBundle(realizedFlow);
		bundlesManager.upgradeSynchronous(osgiBundle);
		execute(realizedFlow);
	}

	public void execute(RealizedFlow realizedFlow) {
		if (log.isTraceEnabled())
			log.trace("Executing " + realizedFlow);

		String moduleName = realizedFlow.getModuleName();
		String moduleVersion = realizedFlow.getModuleVersion();

		ExecutionContext executionContext = findExecutionContext(moduleName,
				moduleVersion);

		// Check whether a descriptor converter is published by this module
		ExecutionFlowDescriptorConverter descriptorConverter = findExecutionFlowDescriptorConverter(
				moduleName, moduleVersion);

		final Map<? extends String, ? extends Object> variablesToAdd;
		if (descriptorConverter != null)
			variablesToAdd = descriptorConverter.convertValues(realizedFlow
					.getFlowDescriptor());
		else
			variablesToAdd = defaultDescriptorConverter
					.convertValues(realizedFlow.getFlowDescriptor());

		executionContext.addVariables(variablesToAdd);

		ExecutionFlow flow = findExecutionFlow(moduleName, moduleVersion,
				realizedFlow.getFlowDescriptor().getName());

		//
		// Actually runs the flow, IN THIS THREAD
		//
		flow.run();
		//
		//
		//
	}

}
