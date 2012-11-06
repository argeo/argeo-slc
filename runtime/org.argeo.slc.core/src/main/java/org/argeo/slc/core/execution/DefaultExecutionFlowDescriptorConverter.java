/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.core.execution;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * Performs conversion in both direction between data exchanged with the agent
 * and the data in the application context.
 */
public class DefaultExecutionFlowDescriptorConverter implements
		ExecutionFlowDescriptorConverter, ApplicationContextAware {
	public final static String REF_VALUE_TYPE_BEAN_NAME = "beanName";

	/** Workaround for https://www.spartadn.com/bugzilla/show_bug.cgi?id=206 */
	private final static String REF_VALUE_INTERNAL = "[internal]";

	private final static Log log = LogFactory
			.getLog(DefaultExecutionFlowDescriptorConverter.class);

	private ApplicationContext applicationContext;

	@SuppressWarnings("unused")
	public Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor) {
		Map<String, Object> values = executionFlowDescriptor.getValues();
		Map<String, Object> convertedValues = new HashMap<String, Object>();
		ExecutionSpec executionSpec = executionFlowDescriptor
				.getExecutionSpec();

		if (executionSpec == null && log.isTraceEnabled())
			log.warn("Execution spec is null for " + executionFlowDescriptor);

		if (values != null && executionSpec != null) {
			values: for (String key : values.keySet()) {
				ExecutionSpecAttribute attribute = executionSpec
						.getAttributes().get(key);

				if (attribute == null)
					throw new SlcException("No spec attribute defined for '"
							+ key + "'");

				if (attribute.getIsConstant())
					continue values;

				Object value = values.get(key);
				if (value instanceof PrimitiveValue) {
					PrimitiveValue primitiveValue = (PrimitiveValue) value;
					// TODO: check class <=> type
					convertedValues.put(key, primitiveValue.getValue());
				} else if (value instanceof RefValue) {
					RefValue refValue = (RefValue) value;
					String type = refValue.getType();
					if (REF_VALUE_TYPE_BEAN_NAME.equals(type)) {
						// FIXME: UI should send all information about spec
						// - targetClass
						// - name
						// String executionSpecName = executionSpec.getName();
						// ExecutionSpec localSpec = (ExecutionSpec)
						// applicationContext
						// .getBean(executionSpecName);
						// RefSpecAttribute localAttr = (RefSpecAttribute)
						// localSpec
						// .getAttributes().get(key);
						// Class<?> targetClass = localAttr.getTargetClass();
						//
						// String primitiveType = PrimitiveUtils
						// .classAsType(targetClass);
						String primitiveType = null;
						if (primitiveType != null) {
							// not active
							String ref = refValue.getRef();
							Object obj = PrimitiveUtils.convert(primitiveType,
									ref);
							convertedValues.put(key, obj);
						} else {
							String ref = refValue.getRef();
							if (ref != null && !ref.equals(REF_VALUE_INTERNAL)) {
								Object obj = null;
								if (applicationContext.containsBean(ref)) {
									obj = applicationContext.getBean(ref);
								} else {
									// FIXME: hack in order to pass primitive
									obj = ref;
								}
								convertedValues.put(key, obj);
							} else {
								log.warn("Cannot interpret " + refValue);
							}
						}
					} else if (PrimitiveUtils.typeAsClass(type) != null) {
						String ref = refValue.getRef();
						Object obj = PrimitiveUtils.convert(type, ref);
						convertedValues.put(key, obj);
					} else {
						throw new UnsupportedException("Ref value type",
								refValue.getType());
					}
				} else {
					// default is to take the value as is
					convertedValues.put(key, value);
				}
			}
		}
		return convertedValues;
	}

	public void addFlowsToDescriptor(ExecutionModuleDescriptor md,
			Map<String, ExecutionFlow> executionFlows) {
		SortedSet<ExecutionFlowDescriptor> set = new TreeSet<ExecutionFlowDescriptor>(
				new ExecutionFlowDescriptorComparator());
		for (String name : executionFlows.keySet()) {
			ExecutionFlow executionFlow = executionFlows.get(name);

			ExecutionFlowDescriptor efd = getExecutionFlowDescriptor(executionFlow);
			ExecutionSpec executionSpec = efd.getExecutionSpec();

			// Add execution spec if necessary
			if (!md.getExecutionSpecs().contains(executionSpec))
				md.getExecutionSpecs().add(executionSpec);

			// Add execution flow
			set.add(efd);
			// md.getExecutionFlows().add(efd);
		}
		md.getExecutionFlows().addAll(set);
	}

	public ExecutionFlowDescriptor getExecutionFlowDescriptor(
			ExecutionFlow executionFlow) {
		if (executionFlow.getName() == null)
			throw new SlcException("Flow name is null: " + executionFlow);
		String name = executionFlow.getName();

		ExecutionSpec executionSpec = executionFlow.getExecutionSpec();
		if (executionSpec == null)
			throw new SlcException("Execution spec is null: " + executionFlow);
		if (executionSpec.getName() == null)
			throw new SlcException("Execution spec name is null: "
					+ executionSpec);

		Map<String, Object> values = new TreeMap<String, Object>();
		for (String key : executionSpec.getAttributes().keySet()) {
			ExecutionSpecAttribute attribute = executionSpec.getAttributes()
					.get(key);

			if (attribute instanceof PrimitiveSpecAttribute) {
				if (executionFlow.isSetAsParameter(key)) {
					Object value = executionFlow.getParameter(key);
					PrimitiveValue primitiveValue = new PrimitiveValue();
					primitiveValue.setType(((PrimitiveSpecAttribute) attribute)
							.getType());
					primitiveValue.setValue(value);
					values.put(key, primitiveValue);
				} else {
					// no need to add a primitive value if it is not set,
					// all necessary information is in the spec
				}
			} else if (attribute instanceof RefSpecAttribute) {
				if (attribute.getIsConstant()) {
					values.put(key, new RefValue(REF_VALUE_INTERNAL));
				} else
					values.put(
							key,
							buildRefValue((RefSpecAttribute) attribute,
									executionFlow, key));
			} else {
				throw new SlcException("Unkown spec attribute type "
						+ attribute.getClass());
			}

		}

		ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(name, values,
				executionSpec);
//		if (executionFlow.getPath() != null)
//			efd.setPath(executionFlow.getPath());
//		else
//			efd.setPath("");

		// Takes description from spring
		BeanFactory bf = getBeanFactory();
		if (bf != null) {
			BeanDefinition bd = getBeanFactory().getBeanDefinition(name);
			efd.setDescription(bd.getDescription());
		}
		return efd;
	}

	@SuppressWarnings(value = { "unchecked" })
	protected RefValue buildRefValue(RefSpecAttribute rsa,
			ExecutionFlow executionFlow, String key) {
		RefValue refValue = new RefValue();
		// FIXME: UI should be able to deal with other types
		refValue.setType(REF_VALUE_TYPE_BEAN_NAME);
		Class<?> targetClass = rsa.getTargetClass();
		String primitiveType = PrimitiveUtils.classAsType(targetClass);
		if (primitiveType != null) {
			if (executionFlow.isSetAsParameter(key)) {
				Object value = executionFlow.getParameter(key);
				refValue.setRef(value.toString());
			}
			refValue.setType(primitiveType);
			return refValue;
		} else {

			if (executionFlow.isSetAsParameter(key)) {
				String ref = null;
				Object value = executionFlow.getParameter(key);
				if (applicationContext == null) {
					log.warn("No application context declared, cannot scan ref value.");
					ref = value.toString();
				} else {

					// look for a ref to the value
					Map<String, Object> beans = getBeanFactory()
							.getBeansOfType(targetClass, false, false);
					// TODO: also check scoped beans
					beans: for (String beanName : beans.keySet()) {
						Object obj = beans.get(beanName);
						if (value instanceof ScopedObject) {
							// don't call methods of the target of the scope
							if (obj instanceof ScopedObject)
								if (value == obj) {
									ref = beanName;
									break beans;
								}
						} else {
							if (obj.equals(value)) {
								ref = beanName;
								break beans;
							}
						}
					}
				}
				if (ref == null) {
					if (log.isTraceEnabled())
						log.trace("Cannot define reference for ref spec attribute "
								+ key
								+ " in "
								+ executionFlow
								+ " ("
								+ rsa
								+ ")."
								+ " If it is an inner bean consider put it frozen.");
					ref = REF_VALUE_INTERNAL;
				} else {
					if (log.isTraceEnabled())
						log.trace(ref
								+ " is the reference for ref spec attribute "
								+ key + " in " + executionFlow + " (" + rsa
								+ ")");
				}
				refValue.setRef(ref);
			}
			return refValue;
		}
	}

	/** @return can be null */
	private ConfigurableListableBeanFactory getBeanFactory() {
		if (applicationContext == null)
			return null;
		return ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();
	}

	/** Must be use within the execution application context */
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	private static class ExecutionFlowDescriptorComparator implements
			Comparator<ExecutionFlowDescriptor> {
		@SuppressWarnings("deprecation")
		public int compare(ExecutionFlowDescriptor o1,
				ExecutionFlowDescriptor o2) {
			// TODO: write unit tests for this

			String name1 = o1.getName();
			String name2 = o2.getName();

			String path1 = o1.getPath();
			String path2 = o2.getPath();

			// Check whether name include path
			int lastIndex1 = name1.lastIndexOf('/');
			// log.debug(name1+", "+lastIndex1);
			if (!StringUtils.hasText(path1) && lastIndex1 >= 0) {
				path1 = name1.substring(0, lastIndex1);
				name1 = name1.substring(lastIndex1 + 1);
			}

			int lastIndex2 = name2.lastIndexOf('/');
			if (!StringUtils.hasText(path2) && lastIndex2 >= 0) {
				path2 = name2.substring(0, lastIndex2);
				name2 = name2.substring(lastIndex2 + 1);
			}

			// Perform the actual comparison
			if (StringUtils.hasText(path1) && StringUtils.hasText(path2)) {
				if (path1.equals(path2))
					return name1.compareTo(name2);
				else if (path1.startsWith(path2))
					return -1;
				else if (path2.startsWith(path1))
					return 1;
				else
					return path1.compareTo(path2);
			} else if (!StringUtils.hasText(path1)
					&& StringUtils.hasText(path2)) {
				return 1;
			} else if (StringUtils.hasText(path1)
					&& !StringUtils.hasText(path2)) {
				return -1;
			} else if (!StringUtils.hasText(path1)
					&& !StringUtils.hasText(path2)) {
				return name1.compareTo(name2);
			} else {
				return 0;
			}
		}

	}
}
