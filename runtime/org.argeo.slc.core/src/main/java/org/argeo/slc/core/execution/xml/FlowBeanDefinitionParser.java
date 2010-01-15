package org.argeo.slc.core.execution.xml;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionFlow;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FlowBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	// private Log log = LogFactory.getLog(FlowBeanDefinitionParser.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		String path = element.getAttribute("path");
		if (StringUtils.hasText(path))
			builder.addPropertyValue("path", path);

		String spec = element.getAttribute("spec");
		if (StringUtils.hasText(spec))
			builder.getBeanDefinition().getConstructorArgumentValues()
					.addGenericArgumentValue(new RuntimeBeanReference(spec));

		String abstrac = element.getAttribute("abstract");
		if (StringUtils.hasText(abstrac))
			builder.setAbstract(Boolean.parseBoolean(abstrac));

		String parent = element.getAttribute("parent");
		if (StringUtils.hasText(parent))
			builder.setParentName(parent);

		List<Element> execElems = new ArrayList<Element>();
		List<Element> argsElems = new ArrayList<Element>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				if (DomUtils.nodeNameEquals(node, "arg"))
					argsElems.add((Element) node);
				else
					execElems.add((Element) node);
			}
		}

		// Arguments
		if (argsElems.size() != 0) {
			ManagedMap args = new ManagedMap(argsElems.size());
			for (Element argElem : argsElems) {
				Object value = NamespaceUtils.parseValue(argElem,
						parserContext, builder.getBeanDefinition(), null);
				if (value != null)
					args.put(argElem.getAttribute("name"), value);
				else
					throw new SlcException("No value defined.");
			}
			builder.getBeanDefinition().getConstructorArgumentValues()
					.addGenericArgumentValue(args);
		}

		// Executables
		if (execElems.size() != 0) {
			ManagedList executables = new ManagedList(execElems.size());
			for (int i = 0; i < execElems.size(); i++) {
				Element child = execElems.get(i);
				String name = child.getLocalName();
				if (DomUtils.nodeNameEquals(child, "bean")
						|| DomUtils.nodeNameEquals(child, "ref")) {
					// Object target = parseBeanReference((Element) child,
					// parserContext, builder);
					executables.add(NamespaceUtils.parseBeanReference(child,
							parserContext, builder.getBeanDefinition()));
				} else if (DomUtils.nodeNameEquals(child, "flow")) {
					throw new SlcException(
							"Nested flows are not yet supported, use a standard ref to another flow.");
				} else {
					throw new SlcException("Unsupported child '" + name + "'");
				}
			}
			builder.addPropertyValue("executables", executables);
		}
	}

	@Override
	protected Class<DefaultExecutionFlow> getBeanClass(Element element) {
		return DefaultExecutionFlow.class;
	}

	// parse nested bean definition
	// private Object parseBeanReference(Element element,
	// ParserContext parserContext, BeanDefinitionBuilder builder) {
	// return parserContext.getDelegate().parsePropertySubElement(element,
	// builder.getBeanDefinition());
	// }

	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

}
