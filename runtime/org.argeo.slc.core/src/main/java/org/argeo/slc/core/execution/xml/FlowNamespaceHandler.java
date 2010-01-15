package org.argeo.slc.core.execution.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class FlowNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("flow", new FlowBeanDefinitionParser());
		registerBeanDefinitionParser("spec", new SpecBeanDefinitionParser());
		registerBeanDefinitionDecoratorForAttribute("as-flow",
				new AsFlowDecorator());
		registerBeanDefinitionParser("param", new ParamDecorator());
	}

}
