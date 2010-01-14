package org.argeo.slc.core.execution.xml;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.core.execution.RefValueChoice;
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

public class SpecBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	private Log log = LogFactory.getLog(SpecBeanDefinitionParser.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		ManagedMap specAttrs = new ManagedMap();

		// Primitives
		for (Element child : (List<Element>) DomUtils
				.getChildElementsByTagName(element, "primitive")) {
			BeanDefinitionBuilder childBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(PrimitiveSpecAttribute.class);
			addAbstractSpecAttributeProperties(childBuilder, child);
			addValue(childBuilder, child, parserContext);

			String type = child.getAttribute("type");
			if (StringUtils.hasText(type))
				childBuilder.addPropertyValue("type", type);

			String name = child.getAttribute("name");
			specAttrs.put(name, childBuilder.getBeanDefinition());
			if (log.isTraceEnabled())
				log.debug("Added primitive attribute " + name);
		}

		// Refs
		for (Element child : (List<Element>) DomUtils
				.getChildElementsByTagName(element, "ref")) {
			BeanDefinitionBuilder childBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(RefSpecAttribute.class);
			addAbstractSpecAttributeProperties(childBuilder, child);
			addValue(childBuilder, child, parserContext);

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

			String name = child.getAttribute("name");
			specAttrs.put(name, childBuilder.getBeanDefinition());
			if (log.isTraceEnabled())
				log.debug("Added spec attribute " + name);
		}

		builder.addPropertyValue("attributes", specAttrs);
	}

	protected void addAbstractSpecAttributeProperties(
			BeanDefinitionBuilder specAttr, Element element) {

		addBooleanProperty("isParameter", specAttr, element);
		addBooleanProperty("isFrozen", specAttr, element);
		addBooleanProperty("isHidden", specAttr, element);
	}

	protected void addValue(BeanDefinitionBuilder specAttr, Element element,
			ParserContext parserContext) {
		Boolean alreadySet = false;
		if (element.hasAttribute("value")) {
			specAttr.addPropertyValue("value", element.getAttribute("value"));
			alreadySet = true;
		}

		if (element.hasAttribute("value-ref")) {
			if (alreadySet)
				throw new SlcException("Multiple value definition for "
						+ specAttr);
			specAttr.addPropertyValue("value", new RuntimeBeanReference(element
					.getAttribute("value-ref")));
		}

		Element valueElem = DomUtils.getChildElementByTagName(element, "value");
		if (valueElem != null) {
			if (alreadySet)
				throw new SlcException("Multiple value definition for "
						+ specAttr);

			NodeList valueChildNd = valueElem.getChildNodes();

			for (int i = 0; i < valueChildNd.getLength(); i++) {
				Node node = valueChildNd.item(i);
				if (node != null && node instanceof Element) {
					specAttr.addPropertyValue("value", parseBeanReference(
							(Element) node, parserContext, specAttr));
					break;
				}
			}
		}
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

	// parse nested bean definition
	private Object parseBeanReference(Element element,
			ParserContext parserContext, BeanDefinitionBuilder builder) {
		return parserContext.getDelegate().parsePropertySubElement(element,
				builder.getBeanDefinition());
	}

	protected boolean shouldGenerateIdAsFallback() {
		return false;
	}

}
