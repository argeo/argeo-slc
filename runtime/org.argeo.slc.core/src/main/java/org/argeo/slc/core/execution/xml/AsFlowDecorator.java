package org.argeo.slc.core.execution.xml;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionFlow;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class AsFlowDecorator implements BeanDefinitionDecorator {

	@SuppressWarnings("unchecked")
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder bean,
			ParserContext ctx) {
		String flowBeanName = ((Attr) node).getValue();
		if (ctx.getRegistry().containsBeanDefinition(flowBeanName))
			throw new SlcException("A bean named " + flowBeanName
					+ " is already defined.");
		BeanDefinitionBuilder flow = BeanDefinitionBuilder
				.rootBeanDefinition(DefaultExecutionFlow.class);
		ManagedList executables = new ManagedList(1);
		executables.add(bean.getBeanDefinition());
		flow.addPropertyValue("executables", executables);
		ctx.getRegistry().registerBeanDefinition(flowBeanName,
				flow.getBeanDefinition());
		return bean;
	}

}
