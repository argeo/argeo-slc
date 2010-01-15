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
