package org.argeo.slc.core.execution.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private Log log = LogFactory.getLog(AsFlowDecorator.class);

	@SuppressWarnings("unchecked")
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder bean,
			ParserContext ctx) {
		String attrValue = ((Attr) node).getValue();
		if (attrValue.charAt(attrValue.length() - 1) == '/')
			throw new SlcException(attrValue + " cannot end with a path");
		int lastSlash = attrValue.lastIndexOf('/');
		String path;
		String flowBeanName;
		if (lastSlash > 0) {
			flowBeanName = attrValue.substring(lastSlash + 1);
			path = attrValue.substring(0, lastSlash);
		} else if (lastSlash == 0) {
			flowBeanName = attrValue.substring(lastSlash + 1);
			path = null;
		} else {
			flowBeanName = attrValue;
			path = null;
		}

		if (log.isTraceEnabled())
			log.debug("path=" + path + ", flowBeanName=" + flowBeanName);

		if (ctx.getRegistry().containsBeanDefinition(flowBeanName))
			throw new SlcException("A bean named " + flowBeanName
					+ " is already defined.");
		BeanDefinitionBuilder flow = BeanDefinitionBuilder
				.rootBeanDefinition(DefaultExecutionFlow.class);
		ManagedList executables = new ManagedList(1);
		executables.add(bean.getBeanDefinition());
		if (path != null)
			flow.addPropertyValue("path", path);
		flow.addPropertyValue("executables", executables);
		ctx.getRegistry().registerBeanDefinition(flowBeanName,
				flow.getBeanDefinition());
		return bean;
	}

}
