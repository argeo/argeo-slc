package org.argeo.slc.core.execution.xml;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Inspired by org.springframework.aop.config.ScopedProxyBeanDefinitionDecorator
 */
public class ExecutionScopeDecorator implements BeanDefinitionDecorator {	
	private static final String PROXY_TARGET_CLASS = "proxy-target-class";	
	
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {
		
		definition.getBeanDefinition().setScope("execution");
		
		// Default: CGLib not used
		boolean proxyTargetClass = false;
		if (node instanceof Element) {
			Element ele = (Element) node;
			if (ele.hasAttribute(PROXY_TARGET_CLASS)) {
				proxyTargetClass = Boolean.valueOf(ele.getAttribute(PROXY_TARGET_CLASS)).booleanValue();
			}
		}
		
		// Register the original bean definition as it will be referenced by the scoped proxy and is relevant for tooling (validation, navigation).
		String targetBeanName = ScopedProxyUtils.getTargetBeanName(definition.getBeanName());
		parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(definition.getBeanDefinition(), targetBeanName));
		
		return ScopedProxyUtils.createScopedProxy(definition, parserContext.getRegistry(), proxyTargetClass);		
	}
}
