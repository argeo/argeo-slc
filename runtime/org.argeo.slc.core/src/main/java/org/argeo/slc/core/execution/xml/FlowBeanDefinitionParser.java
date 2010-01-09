package org.argeo.slc.core.execution.xml;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionFlow;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FlowBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
//	private Log log = LogFactory.getLog(FlowBeanDefinitionParser.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		String path = element.getAttribute("lenient");
		if (StringUtils.hasText(path))
			builder.addPropertyValue("path", path);

		List<Element> children = new ArrayList<Element>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element)
				children.add((Element) node);
		}
		// children.addAll(DomUtils.getChildElementsByTagName(element, "bean"));
		// children.addAll(DomUtils.getChildElementsByTagName(element, "ref"));

		ManagedList executables = new ManagedList(children.size());
		for (int i = 0; i < children.size(); i++) {
			Element child = children.get(i);
			String name = child.getNodeName();
			if ("bean".equals(name) || "ref".equals(name)) {
				Object target = parseBeanReference(element, (Element) child,
						parserContext, builder);
				executables.add(target);
			} else if ("flow".equals(name)) {
				// TODO
			} else {
				throw new SlcException("Unsupported children '" + name + "'");
			}
		}
		builder.addPropertyValue("executables", executables);
	}

	@Override
	protected Class<DefaultExecutionFlow> getBeanClass(Element element) {
		return DefaultExecutionFlow.class;
	}

	// parse nested bean definition
	private Object parseBeanReference(Element parent, Element element,
			ParserContext parserContext, BeanDefinitionBuilder builder) {
		return parserContext.getDelegate().parsePropertySubElement(element,
				builder.getBeanDefinition());
	}

	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

}
