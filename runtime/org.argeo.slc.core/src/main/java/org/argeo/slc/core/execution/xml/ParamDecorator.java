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

package org.argeo.slc.core.execution.xml;

import org.argeo.slc.core.execution.ParameterRef;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class ParamDecorator extends AbstractSingleBeanDefinitionParser {

	// public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder
	// bean,
	// ParserContext ctx) {
	// String paramName = ((Element) node).getAttribute("name");
	// String propertyName = ((Element) node.getParentNode())
	// .getAttribute("name");
	// BeanDefinitionBuilder parameterRef = BeanDefinitionBuilder
	// .genericBeanDefinition(ParameterRef.class);
	// parameterRef.addPropertyReference("instantiationManager",
	// "instantiationManager");
	// parameterRef.addConstructorArgValue(paramName);
	// bean.getBeanDefinition().getPropertyValues().addPropertyValue(
	// propertyName, parameterRef.getBeanDefinition());
	// return bean;
	// }

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		String paramName = element.getAttribute("name");

		String instantationManagerRef = element
				.getAttribute("instantiationManager");
		if (!StringUtils.hasText(instantationManagerRef))
			instantationManagerRef = "instantiationManager";
		builder.addPropertyReference("instantiationManager",
				instantationManagerRef);
		builder.addConstructorArgValue(paramName);
	}

	@Override
	protected Class<ParameterRef> getBeanClass(Element element) {
		return ParameterRef.class;
	}
}
