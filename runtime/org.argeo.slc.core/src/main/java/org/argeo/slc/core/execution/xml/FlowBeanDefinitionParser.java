/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.core.execution.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionFlow;
import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
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

/** Interprets the <flow:flow> tag */
public class FlowBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	private Log log = LogFactory.getLog(FlowBeanDefinitionParser.class);

	/** Whether the user has already be warned on path attribute usage. */
	private Boolean warnedAboutPathAttribute = false;

	@SuppressWarnings("unchecked")
	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		String path = element.getAttribute("path");
		if (StringUtils.hasText(path)) {
			builder.addPropertyValue("path", path);

			// warns user only once
			if (!warnedAboutPathAttribute)
				log.warn("The path=\"\" attribute is deprecated"
						+ " and will be removed in a later release."
						+ " Use <flow:flow name=\"/my/path/flowName\">.");
			warnedAboutPathAttribute = true;
		}

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

		builder.getBeanDefinition().setDescription(
				DomUtils.getChildElementValueByTagName(element, "description"));

		List<Element> argsElems = new ArrayList<Element>();
		List<Element> execElems = new ArrayList<Element>();
		List<Element> specElems = new ArrayList<Element>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				if (DomUtils.nodeNameEquals(node, "arg"))
					argsElems.add((Element) node);
				else if (DomUtils.nodeNameEquals(node, "spec"))
					specElems.add((Element) node);
				else if (!DomUtils.nodeNameEquals(node, "description"))
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

		// Execution specs
		if (StringUtils.hasText(spec) && specElems.size() != 0)
			throw new SlcException("A flow cannot have multiple specs");
		if (specElems.size() == 1) {
			Object specObj = NamespaceUtils.parseBeanOrReference(
					specElems.get(0), parserContext,
					builder.getBeanDefinition());
			builder.getBeanDefinition().getConstructorArgumentValues()
					.addGenericArgumentValue(specObj);
		} else if (specElems.size() > 1)
			throw new SlcException("A flow cannot have multiple specs");

		// Executables
		if (execElems.size() != 0) {
			ManagedList executables = new ManagedList(execElems.size());
			for (Element child : execElems) {
				// child validity check is performed in xsd
				executables.add(NamespaceUtils.parseBeanOrReference(child,
						parserContext, builder.getBeanDefinition()));
			}
			if (executables.size() > 0)
				builder.addPropertyValue("executables", executables);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends ExecutionFlow> getBeanClass(Element element) {
		String clss = element.getAttribute("class");
		if (StringUtils.hasText(clss))
			// TODO: check that it actually works
			try {
				return (Class<? extends ExecutionFlow>) getClass()
						.getClassLoader().loadClass(clss);
			} catch (ClassNotFoundException e) {
				try {
					return (Class<? extends ExecutionFlow>) Thread
							.currentThread().getContextClassLoader()
							.loadClass(clss);
				} catch (ClassNotFoundException e1) {
					throw new SlcException("Cannot load class " + clss, e);
				}
			}
		else
			return DefaultExecutionFlow.class;
	}

	// parse nested bean definition
	// private Object parseBeanReference(Element element,
	// ParserContext parserContext, BeanDefinitionBuilder builder) {
	// return parserContext.getDelegate().parsePropertySubElement(element,
	// builder.getBeanDefinition());
	// }

	@Override
	protected String resolveId(Element element,
			AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {
		String name = element.getAttribute("name");
		if (StringUtils.hasText(name)) {
			return name;
		} else {
			return super.resolveId(element, definition, parserContext);
		}
	}

	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

}
