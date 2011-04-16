/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.AbstractExecutionModulesManager;
import org.argeo.slc.core.execution.DefaultExecutionFlowDescriptorConverter;
import org.argeo.slc.deploy.ModuleDescriptor;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesListener;
import org.argeo.slc.process.RealizedFlow;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;

public class OsgiExecutionModulesManager extends
		AbstractExecutionModulesManager implements OsgiServiceLifecycleListener {

	private final static Log log = LogFactory
			.getLog(OsgiExecutionModulesManager.class);

	private BundlesManager bundlesManager;
	private Map<OsgiBundle, ExecutionContext> executionContexts = new HashMap<OsgiBundle, ExecutionContext>();
	private Map<OsgiBundle, ExecutionFlowDescriptorConverter> executionFlowDescriptorConverters = new HashMap<OsgiBundle, ExecutionFlowDescriptorConverter>();
	private Map<OsgiBundle, Set<ExecutionFlow>> executionFlows = new HashMap<OsgiBundle, Set<ExecutionFlow>>();
	private ExecutionFlowDescriptorConverter defaultDescriptorConverter = new DefaultExecutionFlowDescriptorConverter();

	public synchronized ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
		OsgiBundle osgiBundle = null;
		BasicNameVersion nameVersion = new BasicNameVersion(moduleName, version);
		bundles: for (Iterator<OsgiBundle> iterator = executionContexts
				.keySet().iterator(); iterator.hasNext();) {
			OsgiBundle ob = iterator.next();
			if (ob.equals(nameVersion)) {
				osgiBundle = ob;
				break bundles;
			}
		}
		if (osgiBundle == null)
			throw new SlcException("No execution module registered for "
					+ nameVersion);
		md.setName(osgiBundle.getName());
		md.setVersion(osgiBundle.getVersion());
		md.setLabel(osgiBundle.getLabel());
		md.setDescription(osgiBundle.getDescription());

		ExecutionFlowDescriptorConverter executionFlowDescriptorConverter = getExecutionFlowDescriptorConverter(
				moduleName, version);
		if (executionFlowDescriptorConverter == null)
			throw new SlcException("No flow converter found.");
		executionFlowDescriptorConverter.addFlowsToDescriptor(md,
				listFlows(moduleName, version));
		return md;
	}

	public synchronized List<ExecutionModuleDescriptor> listExecutionModules() {
		List<ExecutionModuleDescriptor> descriptors = new ArrayList<ExecutionModuleDescriptor>();

		for (Iterator<OsgiBundle> iterator = executionContexts.keySet()
				.iterator(); iterator.hasNext();) {
			OsgiBundle osgiBundle = iterator.next();
			ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
			setMetadataFromBundle(md,
					bundlesManager.findRelatedBundle(osgiBundle));
			descriptors.add(md);
		}
		return descriptors;
	}

	protected synchronized Map<String, ExecutionFlow> listFlows(
			String moduleName, String moduleVersion) {

		Map<String, ExecutionFlow> flows = new HashMap<String, ExecutionFlow>();
		OsgiBundle key = new OsgiBundle(moduleName, moduleVersion);
		if (!executionFlows.containsKey(key))
			return flows;
		Set<ExecutionFlow> flowsT = executionFlows.get(key);
		for (ExecutionFlow flow : flowsT)
			flows.put(flow.getName(), flow);
		return flows;
	}

	protected ExecutionFlow findExecutionFlow(String moduleName,
			String moduleVersion, String flowName) {
		String filter = "(&(Bundle-SymbolicName=" + moduleName
				+ ")(org.springframework.osgi.bean.name=" + flowName + "))";
		return bundlesManager.getSingleServiceStrict(ExecutionFlow.class,
				filter);
	}

	protected ExecutionContext findExecutionContext(String moduleName,
			String moduleVersion) {
		String filter = "(&(Bundle-SymbolicName=" + moduleName
				+ ")(Bundle-Version=" + moduleVersion + "))";
		return bundlesManager.getSingleServiceStrict(ExecutionContext.class,
				filter);
	}

	protected ExecutionFlowDescriptorConverter findExecutionFlowDescriptorConverter(
			String moduleName, String moduleVersion) {

		String filter = "(&(Bundle-SymbolicName=" + moduleName
				+ ")(Bundle-Version=" + moduleVersion + "))";
		return bundlesManager.getSingleService(
				ExecutionFlowDescriptorConverter.class, filter);
	}

	public void setBundlesManager(BundlesManager bundlesManager) {
		this.bundlesManager = bundlesManager;
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
			log.warn("Could not find any execution module matching these requirements.");
			return null;
		}
	}

	public void upgrade(NameVersion nameVersion) {
		OsgiBundle osgiBundle = new OsgiBundle(nameVersion);
		bundlesManager.upgradeSynchronous(osgiBundle);
	}

	protected synchronized ExecutionFlowDescriptorConverter getExecutionFlowDescriptorConverter(
			String moduleName, String moduleVersion) {
		OsgiBundle osgiBundle = new OsgiBundle(moduleName, moduleVersion);
		if (executionFlowDescriptorConverters.containsKey(osgiBundle))
			return executionFlowDescriptorConverters.get(osgiBundle);
		else
			return defaultDescriptorConverter;
	}

	public ModuleDescriptor getModuleDescriptor(String moduleName,
			String version) {
		return getExecutionModuleDescriptor(moduleName, version);
	}

	public List<ModuleDescriptor> listModules() {
		Bundle[] bundles = bundlesManager.getBundleContext().getBundles();
		List<ModuleDescriptor> lst = new ArrayList<ModuleDescriptor>();
		for (Bundle bundle : bundles) {
			ModuleDescriptor moduleDescriptor = new ModuleDescriptor();
			setMetadataFromBundle(moduleDescriptor, bundle);
			lst.add(moduleDescriptor);
		}
		return lst;
	}

	protected void setMetadataFromBundle(ModuleDescriptor md, Bundle bundle) {
		Bundle bdl = bundle;
		if (bdl == null) {
			if (md.getName() == null || md.getVersion() == null)
				throw new SlcException("Name and version not available.");

			Bundle[] bundles = bundlesManager.getBundleContext().getBundles();
			for (Bundle b : bundles) {
				if (b.getSymbolicName().equals(md.getName())
						&& md.getVersion().equals(
								getHeaderSafe(b, Constants.BUNDLE_VERSION))) {
					bdl = b;
					break;
				}
			}

		}

		if (bdl == null)
			throw new SlcException("Cannot find bundle.");

		md.setName(bdl.getSymbolicName());
		md.setVersion(getHeaderSafe(bdl, Constants.BUNDLE_VERSION));
		md.setLabel(getHeaderSafe(bdl, Constants.BUNDLE_NAME));
		md.setDescription(getHeaderSafe(bdl, Constants.BUNDLE_DESCRIPTION));
	}

	private String getHeaderSafe(Bundle bundle, Object key) {
		Object obj = bundle.getHeaders().get(key);
		if (obj == null)
			return null;
		else
			return obj.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void bind(Object service, Map properties)
			throws Exception {
		if (service instanceof ExecutionContext) {
			ExecutionContext executionContext = (ExecutionContext) service;
			OsgiBundle osgiBundle = asOsgiBundle(properties);
			Bundle bundle = bundlesManager.findRelatedBundle(osgiBundle);
			osgiBundle.setLabel(getHeaderSafe(bundle, Constants.BUNDLE_NAME));
			osgiBundle.setDescription(getHeaderSafe(bundle,
					Constants.BUNDLE_DESCRIPTION));
			executionContexts.put(osgiBundle, executionContext);
			if (log.isTraceEnabled())
				log.debug("Registered execution context from " + osgiBundle);
			// Notify
			for (ExecutionModulesListener listener : getExecutionModulesListeners())
				listener.executionModuleAdded(osgiBundle, executionContext);

		} else if (service instanceof ExecutionFlow) {
			ExecutionFlow executionFlow = (ExecutionFlow) service;
			OsgiBundle osgiBundle = asOsgiBundle(properties);
			if (!executionFlows.containsKey(osgiBundle)) {
				executionFlows.put(osgiBundle, new HashSet());
			}
			executionFlows.get(osgiBundle).add(executionFlow);
			if (log.isTraceEnabled())
				log.debug("Registered " + executionFlow + " from " + osgiBundle);
			for (ExecutionModulesListener listener : getExecutionModulesListeners())
				listener.executionFlowAdded(osgiBundle, executionFlow);

		} else if (service instanceof ExecutionFlowDescriptorConverter) {
			ExecutionFlowDescriptorConverter executionFlowDescriptorConverter = (ExecutionFlowDescriptorConverter) service;
			OsgiBundle osgiBundle = asOsgiBundle(properties);
			executionFlowDescriptorConverters.put(osgiBundle,
					executionFlowDescriptorConverter);
			if (log.isTraceEnabled())
				log.debug("Registered execution flow descriptor converter from "
						+ osgiBundle);
		} else {
			// ignore
		}
	}

	@SuppressWarnings("rawtypes")
	public synchronized void unbind(Object service, Map properties)
			throws Exception {
		if (service instanceof ExecutionContext) {
			OsgiBundle osgiBundle = asOsgiBundle(properties);
			if (executionContexts.containsKey(osgiBundle)) {
				ExecutionContext executionContext = executionContexts
						.remove(osgiBundle);
				if (log.isTraceEnabled())
					log.debug("Removed execution context from " + osgiBundle);
				// Notify
				for (ExecutionModulesListener listener : getExecutionModulesListeners())
					listener.executionModuleRemoved(osgiBundle,
							executionContext);
			}
		} else if (service instanceof ExecutionFlow) {
			ExecutionFlow executionFlow = (ExecutionFlow) service;
			OsgiBundle osgiBundle = asOsgiBundle(properties);
			if (executionFlows.containsKey(osgiBundle)) {
				Set flows = executionFlows.get(osgiBundle);
				flows.remove(executionFlow);
				if (log.isTraceEnabled())
					log.debug("Removed " + executionFlow + " from "
							+ osgiBundle);
				if (flows.size() == 0) {
					executionFlows.remove(osgiBundle);
					if (log.isTraceEnabled())
						log.debug("Removed flows set from " + osgiBundle);
				}
				for (ExecutionModulesListener listener : getExecutionModulesListeners())
					listener.executionFlowRemoved(osgiBundle, executionFlow);
			}
		} else if (service instanceof ExecutionFlowDescriptorConverter) {
			OsgiBundle osgiBundle = asOsgiBundle(properties);
			if (executionFlowDescriptorConverters.containsKey(osgiBundle)) {
				executionFlowDescriptorConverters.remove(osgiBundle);
				if (log.isTraceEnabled())
					log.debug("Removed execution flow descriptor converter from "
							+ osgiBundle);
			}
		} else {
			// ignore
		}
	}

	@SuppressWarnings("rawtypes")
	private OsgiBundle asOsgiBundle(Map properties) {
		String bundleSymbolicName = checkAndGet(Constants.BUNDLE_SYMBOLICNAME,
				properties);
		String bundleVersion = checkAndGet(Constants.BUNDLE_VERSION, properties);
		return new OsgiBundle(bundleSymbolicName, bundleVersion);
	}

	@SuppressWarnings("rawtypes")
	private String checkAndGet(Object key, Map properties) {
		if (!properties.containsKey(key) || properties.get(key) == null)
			throw new SlcException(key + " not set in " + properties);
		else
			return properties.get(key).toString();
	}

	public void setDefaultDescriptorConverter(
			ExecutionFlowDescriptorConverter defaultDescriptorConverter) {
		this.defaultDescriptorConverter = defaultDescriptorConverter;
	}
}
