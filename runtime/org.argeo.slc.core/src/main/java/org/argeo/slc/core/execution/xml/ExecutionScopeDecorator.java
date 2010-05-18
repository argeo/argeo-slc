package org.argeo.slc.core.execution.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;

/**
 * Inspired by org.springframework.aop.config.AopNamespaceHandler
 * Conceived to replace Element "aop:scoped-proxy" by an attribute.
 * Does not work correctly with other attribute decorators (e.g. 
 * p namespace) since this decorator needs to be called after all
 * properties have been set on target bean. 
 */
public class ExecutionScopeDecorator implements BeanDefinitionDecorator {
	private Log log = LogFactory.getLog(ExecutionScopeDecorator.class);
	
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {
		
		Boolean isVar = Boolean.valueOf(node.getNodeValue());
				
		if(isVar) {
			definition.getBeanDefinition().setScope("execution");
			
			boolean proxyTargetClass = true;
			
			// Register the original bean definition as it will be referenced by the scoped proxy and is relevant for tooling (validation, navigation).
			String targetBeanName = ScopedProxyUtils.getTargetBeanName(definition.getBeanName());
			parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(definition.getBeanDefinition(), targetBeanName));
			
			log.debug("Decorating bean " + definition.getBeanName());
			
			return ScopedProxyUtils.createScopedProxy(definition, parserContext.getRegistry(), proxyTargetClass);		
		}
		else {
			return definition;
		}
	}
}
