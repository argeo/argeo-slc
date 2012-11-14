/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
