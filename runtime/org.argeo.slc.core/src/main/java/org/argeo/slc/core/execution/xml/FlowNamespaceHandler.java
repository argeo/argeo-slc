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
