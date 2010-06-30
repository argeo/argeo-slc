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

package org.argeo.slc.osgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.osgi.service.exporter.support.ExportContextClassLoader;
import org.springframework.osgi.service.exporter.support.OsgiServiceFactoryBean;

@SuppressWarnings(value = { "unchecked" })
public class MultipleServiceExporterPostProcessor implements
		BeanFactoryPostProcessor, Ordered {
	private final static Log log = LogFactory
			.getLog(MultipleServiceExporterPostProcessor.class);

	private List<Class> interfaces = new ArrayList<Class>();

	private Class osgiServiceFactoryClass = OsgiServiceFactoryBean.class;

	private Boolean useServiceProviderContextClassLoader = false;
	
	private int order = Ordered.LOWEST_PRECEDENCE;

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof BeanDefinitionRegistry)) {
			throw new SlcException("Can only work on "
					+ BeanDefinitionRegistry.class);
		}

		long begin = System.currentTimeMillis();

		// Merge all beans implementing these interfaces
		Set<String> beanNames = new HashSet<String>();
		for (Class clss : interfaces) {
			String[] strs = beanFactory.getBeanNamesForType(clss, true, false);
			beanNames.addAll(Arrays.asList(strs));
		}

		// Register service factory beans for them
		for (String beanName : beanNames) {
			MutablePropertyValues mpv = new MutablePropertyValues();
			mpv.addPropertyValue("interfaces", interfaces.toArray());
			mpv.addPropertyValue("targetBeanName", beanName);
			if (useServiceProviderContextClassLoader)
				mpv.addPropertyValue("contextClassLoader",
						ExportContextClassLoader.SERVICE_PROVIDER);
			RootBeanDefinition bd = new RootBeanDefinition(
					osgiServiceFactoryClass, mpv);

			String exporterBeanName = "osgiService." + beanName;
			if (log.isTraceEnabled())
				log.debug("Registering OSGi service exporter "
						+ exporterBeanName);
			((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
					exporterBeanName, bd);
		}

		long end = System.currentTimeMillis();
		if (log.isTraceEnabled())
			log.debug("Multiple services exported in " + (end - begin)
					+ " ms in bundle.");

	}

	public void setInterfaces(List<Class> interfaces) {
		this.interfaces = interfaces;
	}

	public void setOsgiServiceFactoryClass(Class osgiServiceFactoryClass) {
		this.osgiServiceFactoryClass = osgiServiceFactoryClass;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setUseServiceProviderContextClassLoader(
			Boolean useServiceProviderContextClassLoader) {
		this.useServiceProviderContextClassLoader = useServiceProviderContextClassLoader;
	}

}
