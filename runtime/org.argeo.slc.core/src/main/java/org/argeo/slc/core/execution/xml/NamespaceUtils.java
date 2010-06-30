/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import org.argeo.slc.SlcException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities to simplify common tasks when interpreting a custom namespace and
 * converting it into bean definitions.
 */
public class NamespaceUtils {
	// private final static Log log = LogFactory.getLog(NamespaceUtils.class);

	/**
	 * Returns the value defined either: directly by the the 'value' attribute,
	 * as reference by the 'ref' attribute or as a nested bean.
	 */
	public static Object parseValue(Element element,
			ParserContext parserContext,
			BeanDefinition containingBeanDefintion, String valueTagName) {
		Object value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		}

		if (element.hasAttribute("ref")) {
			if (value != null)
				throw new SlcException("Multiple value definition for "
						+ element);
			value = new RuntimeBeanReference(element.getAttribute("ref"));
		}

		Element uniqueSubElem = null;
		if (valueTagName != null) {
			Element valueElem = DomUtils.getChildElementByTagName(element,
					valueTagName);
			if (valueElem != null) {
				uniqueSubElem = findUniqueSubElement(valueElem);
				if (uniqueSubElem == null)
					throw new SlcException("No subelement found under "
							+ valueElem);
			}
		} else {// no intermediary tag
			uniqueSubElem = findUniqueSubElement(element);
		}

		if (uniqueSubElem != null) {
			if (value != null)
				throw new SlcException("Multiple value definition for "
						+ element);
			value = parseBeanOrReference(uniqueSubElem, parserContext,
					containingBeanDefintion);
		}
		return value;
	}

	public static Element findUniqueSubElement(Element element) {
		NodeList childNodes = element.getChildNodes();

		Element uniqueSubElem = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node != null && node instanceof Element) {
				if (uniqueSubElem == null)
					uniqueSubElem = (Element) node;
				else
					throw new SlcException(
							"There are more than one sub element under "
									+ element);
			}
		}
		return uniqueSubElem;
	}

	public static Object parseBeanOrReference(Element element,
			ParserContext parserContext, BeanDefinition beanDefinition) {
		// return parserContext.getDelegate().parsePropertySubElement(element,
		// beanDefinition);

		BeanDefinitionParserDelegate deleg = parserContext.getDelegate();
		// if ("bean".equals(element.getNodeName()))
		// return deleg.parseBeanDefinitionElement(element, beanDefinition);
		// else
		return deleg.parsePropertySubElement(element, beanDefinition);
	}
}
