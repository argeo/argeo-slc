package org.argeo.slc.core.execution.xml;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.core.execution.RefValueChoice;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SpecBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	private Log log = LogFactory.getLog(SpecBeanDefinitionParser.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		ManagedMap attributes = new ManagedMap();

		// Primitives
		for (Element child : (List<Element>) DomUtils
				.getChildElementsByTagName(element, "primitive")) {
			BeanDefinitionBuilder childBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(PrimitiveSpecAttribute.class);
			addCommonProperties(child, parserContext, childBuilder);

			String type = child.getAttribute("type");
			if (StringUtils.hasText(type))
				childBuilder.addPropertyValue("type", type);

			putInAttributes(attributes, child,
					childBuilder.getBeanDefinition(), "primitive");
		}

		// Refs
		for (Element child : (List<Element>) DomUtils
				.getChildElementsByTagName(element, "ref")) {
			BeanDefinitionBuilder childBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(RefSpecAttribute.class);
			addCommonProperties(child, parserContext, childBuilder);

			String targetClassName = child.getAttribute("targetClass");
			if (StringUtils.hasText(targetClassName))
				childBuilder.addPropertyValue("targetClass", targetClassName);

			// Choices
			NodeList choicesNd = child.getElementsByTagName("choices");
			if (choicesNd.getLength() > 0) {
				Element choicesElem = (Element) choicesNd.item(0);
				List choices = DomUtils.getChildElementsByTagName(choicesElem,
						"choice");
				ManagedList choiceBeans = new ManagedList(choices.size());
				for (Element choiceElem : (List<Element>) choices) {
					BeanDefinitionBuilder choiceBuilder = BeanDefinitionBuilder
							.genericBeanDefinition(RefValueChoice.class);
					choiceBuilder.addPropertyValue("name", choiceElem
							.getAttribute("name"));
					String desc = choiceElem.getAttribute("description");
					if (StringUtils.hasText(desc))
						choiceBuilder.addPropertyValue("description", desc);

					choiceBeans.add(choiceBuilder.getBeanDefinition());
				}

			}

			putInAttributes(attributes, child,
					childBuilder.getBeanDefinition(), "ref");
		}

		builder.addPropertyValue("attributes", attributes);
	}

	protected void addCommonProperties(Element element,
			ParserContext parserContext, BeanDefinitionBuilder specAttr) {
		addBooleanProperty("isParameter", specAttr, element);
		addBooleanProperty("isFrozen", specAttr, element);
		addBooleanProperty("isHidden", specAttr, element);

		Object value = NamespaceUtils.parseValue(element, parserContext,
				specAttr.getBeanDefinition(), "value");
		if (value != null)
			specAttr.addPropertyValue("value", value);

	}

	@SuppressWarnings("unchecked")
	protected void putInAttributes(ManagedMap attributes, Element child,
			BeanDefinition beanDefinition, String nature) {
		String name = child.getAttribute("name");
		attributes.put(name, beanDefinition);
		if (log.isTraceEnabled())
			log.debug("Added " + nature + " attribute " + name);

	}

	private void addBooleanProperty(String name,
			BeanDefinitionBuilder specAttr, Element element) {
		String bool = element.getAttribute(name);
		if (StringUtils.hasText(bool))
			specAttr.addPropertyValue(name, Boolean.parseBoolean(bool));

	}

	@Override
	protected Class<DefaultExecutionSpec> getBeanClass(Element element) {
		return DefaultExecutionSpec.class;
	}

	protected boolean shouldGenerateIdAsFallback() {
		return false;
	}

}
