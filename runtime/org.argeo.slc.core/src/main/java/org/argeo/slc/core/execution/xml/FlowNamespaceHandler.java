package org.argeo.slc.core.execution.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class FlowNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("flow", new FlowBeanDefinitionParser());
		registerBeanDefinitionParser("spec", new SpecBeanDefinitionParser());
		registerBeanDefinitionDecoratorForAttribute("as-flow",
				new AsFlowDecorator());
		registerBeanDefinitionParser("param", new ParamDecorator());
		 
		// The objective was to replace
		// - attribute scope="execution"
		// - and element "aop:scoped-proxy" 
		// by a single attribute, using an attribute decorator 
		// this does not work correctly with other attribute decorators (e.g. 
		// p namespace) since this decorator needs to be called after all
		// properties have been set on target bean. 
		// It works properly with element decorators (called after all attribute
		// decorators
		registerBeanDefinitionDecorator("variable", new ExecutionScopeDecorator());
	}

}
