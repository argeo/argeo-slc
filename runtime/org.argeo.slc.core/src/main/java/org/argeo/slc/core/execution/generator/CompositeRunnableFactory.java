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

package org.argeo.slc.core.execution.generator;

import java.util.Map;

import org.argeo.slc.SlcException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Composite <code>RunnableFactory</code>, redirecting the Runnable 
 * creation to on of the configured <code>RunnableFactory</code> depending
 * on an entry of the data of the <code>RunnableDataNode</code>.
 */
public class CompositeRunnableFactory implements RunnableFactory {

	/**
	 * Key used to access factory ID in the data of the <code>RunnableDataNode</code>
	 */
	private String factoryKey;

	/**
	 * Maps a factory ID to an ExecutionFlowFactory
	 */
	private Map<String, RunnableFactory> factories;

	public void createAndRegisterRunnable(RunnableDataNode node,
			BeanDefinitionRegistry beanDefinitionRegistry) {
		findFactory(node).createAndRegisterRunnable(node, beanDefinitionRegistry);
	}	
	
	/**
	 * Finds the <code>RunnableFactory</code> to use for a <code>RunnableDataNode</code>
	 * @param node
	 * @return the <code>RunnableFactory</code> to use for the <code>RunnableDataNode</code>
	 */
	private RunnableFactory findFactory(RunnableDataNode node) {
		// get the factory ID from the data of the RunnableDescriptor
		Map<String, Object> data = node.getData();
		if (!data.containsKey(factoryKey)) {
			throw new SlcException("No data value for key '" + factoryKey + "'");
		}
		String factoryId = data.get(factoryKey).toString();
		
		// see if we have a factory for the factory ID
		if ((factories != null) && factories.containsKey(factoryId)) {
			return factories.get(factoryId);
		}
		// if not, look for a bean of name equals to the factory ID
		else {
			throw new SlcException("Not implemented");
		}		
	}
	
	public void setFactoryKey(String factoryKey) {
		this.factoryKey = factoryKey;
	}

	public void setFactories(Map<String, RunnableFactory> factories) {
		this.factories = factories;
	}


}
