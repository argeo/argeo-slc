/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.execution.AbstractSpringExecutionModule;
import org.argeo.slc.execution.ExecutionContext;

@Deprecated
public class OsgiExecutionModule extends AbstractSpringExecutionModule {
	private final static Log log = LogFactory.getLog(OsgiExecutionModule.class);

	public OsgiExecutionModule() {
		log.error("######## ERROR - DEPRECATED APPROACH USED ########");
		log.error(OsgiExecutionModule.class.getName() + " is deprecated. ");
		log
				.error("It will be removed in the next release. Remove its bean definition.");
		log
				.error("And replace: <service interface=\"org.argeo.slc.execution.ExecutionModule\" ref=\"executionModule\" />");
		log
				.error("by: <beans:import resource=\"classpath:org/argeo/slc/osgi/execution/spring.xml\" /> ");
		log.error("in osgi.xml.\n\n");
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		// do nothing, just for compatibility
	}

	/*
	 * private BundleContext bundleContext;
	 * 
	 * @Override public void execute(ExecutionFlowDescriptor
	 * executionFlowDescriptor) { if (descriptorConverter != null)
	 * executionContext.addVariables(descriptorConverter
	 * .convertValues(executionFlowDescriptor));
	 * 
	 * ExecutionFlow flow = findExecutionFlow(getName(), getVersion(),
	 * executionFlowDescriptor.getName()); flow.run(); }
	 * 
	 * @Override protected Map<String, ExecutionFlow> listFlows() { String
	 * filter = "(org.argeo.slc.execution.module.name=" + getName() + ")";
	 * ServiceReference[] sfs; try { sfs =
	 * bundleContext.getServiceReferences(ExecutionFlow.class .getName(),
	 * filter); } catch (InvalidSyntaxException e) { throw new SlcException(
	 * "Cannot retrieve service reference for flow " + filter, e); }
	 * 
	 * Map<String, ExecutionFlow> flows = new HashMap<String, ExecutionFlow>();
	 * for (ServiceReference sf : sfs) { ExecutionFlow flow = (ExecutionFlow)
	 * bundleContext.getService(sf); flows.put(flow.getName(), flow); } return
	 * flows; }
	 * 
	 * public String getName() { return
	 * bundleContext.getBundle().getSymbolicName(); }
	 * 
	 * public String getVersion() { return
	 * bundleContext.getBundle().getHeaders().get("Bundle-Version") .toString();
	 * }
	 * 
	 * public void setBundleContext(BundleContext bundleContext) {
	 * this.bundleContext = bundleContext; }
	 * 
	 * protected ExecutionFlow findExecutionFlow(String moduleName, String
	 * moduleVersion, String flowName) { String filter =
	 * "(&(org.argeo.slc.execution.module.name=" + moduleName +
	 * ")(org.argeo.slc.execution.flow.name=" + flowName + "))";
	 * log.debug("OSGi filter: " + filter);
	 * 
	 * Assert.isTrue(OsgiFilterUtils.isValidFilter(filter), "valid filter");
	 * ServiceReference[] sfs; try { sfs =
	 * bundleContext.getServiceReferences(ExecutionFlow.class .getName(),
	 * filter); } catch (InvalidSyntaxException e) { throw new
	 * SlcException("Cannot retrieve service reference for " + filter, e); }
	 * 
	 * if (sfs == null || sfs.length == 0) throw new
	 * SlcException("No execution flow found for " + filter); else if
	 * (sfs.length > 1) throw new
	 * SlcException("More than one execution flow found for " + filter); return
	 * (ExecutionFlow) bundleContext.getService(sfs[0]); }
	 */

}
