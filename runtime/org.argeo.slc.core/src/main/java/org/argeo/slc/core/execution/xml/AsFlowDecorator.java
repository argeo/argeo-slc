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
package org.argeo.slc.core.execution.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionFlow;
import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/** Publishes a {@link Runnable} as an {@link ExecutionFlow} */
public class AsFlowDecorator implements BeanDefinitionDecorator {
	private Log log = LogFactory.getLog(AsFlowDecorator.class);

	@SuppressWarnings("unchecked")
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder bean,
			ParserContext ctx) {
		String attrValue = ((Attr) node).getValue();
		if (attrValue.charAt(attrValue.length() - 1) == '/')
			throw new SlcException(attrValue + " cannot end with a path");
		final String flowBeanName = attrValue;

		if (log.isTraceEnabled())
			log.trace("flowBeanName=" + flowBeanName);

		if (ctx.getRegistry().containsBeanDefinition(flowBeanName))
			throw new SlcException("A bean named " + flowBeanName
					+ " is already defined.");
		BeanDefinitionBuilder flow = BeanDefinitionBuilder
				.rootBeanDefinition(DefaultExecutionFlow.class);
		ManagedList executables = new ManagedList(1);

		String beanName = bean.getBeanName();
		if (beanName == null)
			executables.add(bean.getBeanDefinition());
		else
			executables.add(new RuntimeBeanReference(beanName));

		// if (path != null)
		// flow.addPropertyValue("path", path);
		flow.addPropertyValue("executables", executables);

		if (beanName != null)
			ctx.getRegistry().registerBeanDefinition(flowBeanName,
					flow.getBeanDefinition());
		return bean;
	}

}
