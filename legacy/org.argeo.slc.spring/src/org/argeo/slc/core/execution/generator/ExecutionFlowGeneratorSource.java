package org.argeo.slc.core.execution.generator;

import java.util.List;

/**
 * Provides 2 types of information required by an <code>ExecutionFlowGenerator</code>: 
 * a list of <code>RunnableCallFlowDescriptor</code> used to create <code>RunnableCallFlow</code>
 * and a list of <code>RunnableDataNode</code> used to create any kind of flow via a factory.
 */
public interface ExecutionFlowGeneratorSource {
	
	/**
	 * @return a list of <code>RunnableCallFlowDescriptor</code> used 
	 * by a <code>ExecutionFlowGenerator</code> to create <code>RunnableCallFlow</code>
	 */
	public List<RunnableCallFlowDescriptor> getRunnableCallFlowDescriptors();
	
	/**
	 * @return a list of <code>RunnableDataNode</code> used 
	 * by a <code>ExecutionFlowGenerator</code> to create any kind of flow via a factory
	 */
	public List<RunnableDataNode> getRunnableDataNodes();
	
}
