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
package org.argeo.slc.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

/** Publishes beans of the application context as OSGi services. */
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class MultipleServiceExporterPostProcessor implements
		ApplicationListener, Ordered {
	private final static Log log = LogFactory
			.getLog(MultipleServiceExporterPostProcessor.class);

	private List<Class> interfaces = new ArrayList<Class>();

	private int order = Ordered.LOWEST_PRECEDENCE;

	private BundleContext bundleContext = null;

	// private Class osgiServiceFactoryClass = OsgiServiceFactoryBean.class;
	// private Boolean useServiceProviderContextClassLoader = false;

	public void onApplicationEvent(ApplicationEvent event) {
		Map<String, Object> beans = new HashMap<String, Object>();
		if (event instanceof ContextRefreshedEvent) {
			if (bundleContext != null) {
				for (Class clss : interfaces) {
					ApplicationContext ac = ((ContextRefreshedEvent) event)
							.getApplicationContext();
					beans.putAll(ac.getBeansOfType(clss, false, false));
				}

				int count = 0;
				for (String beanName : beans.keySet()) {
					Object bean = beans.get(beanName);
					List<String> classes = new ArrayList<String>();
					for (Class clss : interfaces) {
						if (clss.isAssignableFrom(bean.getClass())) {
							classes.add(clss.getName());
						}
					}
					Properties props = new Properties();
					Bundle bundle = bundleContext.getBundle();
					props.put(Constants.BUNDLE_SYMBOLICNAME,
							bundle.getSymbolicName());
					props.put(Constants.BUNDLE_VERSION, bundle.getVersion());
					// retrocompatibility with pre-1.0:
					props.put("org.springframework.osgi.bean.name", beanName);
					bundleContext.registerService(
							classes.toArray(new String[classes.size()]), bean,
							props);
					count++;
				}
				if (log.isTraceEnabled())
					log.trace("Published " + count + " " + interfaces
							+ " as OSGi services from bundle "
							+ bundleContext.getBundle().getSymbolicName() + " "
							+ bundleContext.getBundle().getVersion());
				// note: the services will be automatically unregistered when
				// the bundle will be stopped
			}
		}
	}

	// public void postProcessBeanFactory(
	// ConfigurableListableBeanFactory beanFactory) throws BeansException {
	// if (!(beanFactory instanceof BeanDefinitionRegistry)) {
	// throw new SlcException("Can only work on "
	// + BeanDefinitionRegistry.class);
	// }
	//
	// long begin = System.currentTimeMillis();
	//
	// // Merge all beans implementing these interfaces
	// Set<String> beanNames = new HashSet<String>();
	// for (Class clss : interfaces) {
	// String[] strs = beanFactory.getBeanNamesForType(clss, true, false);
	// beanNames.addAll(Arrays.asList(strs));
	// }
	//
	// // Register service factory beans for them
	// for (String beanName : beanNames) {
	// MutablePropertyValues mpv = new MutablePropertyValues();
	// mpv.addPropertyValue("interfaces", interfaces.toArray());
	// mpv.addPropertyValue("targetBeanName", beanName);
	// if (useServiceProviderContextClassLoader)
	// mpv.addPropertyValue("contextClassLoader",
	// ExportContextClassLoader.SERVICE_PROVIDER);
	// RootBeanDefinition bd = new RootBeanDefinition(
	// osgiServiceFactoryClass, mpv);
	//
	// String exporterBeanName = "osgiService." + beanName;
	// if (log.isTraceEnabled())
	// log.debug("Registering OSGi service exporter "
	// + exporterBeanName);
	// ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
	// exporterBeanName, bd);
	// }
	//
	// long end = System.currentTimeMillis();
	// if (log.isTraceEnabled())
	// log.debug("Multiple services exported in " + (end - begin)
	// + " ms in bundle.");
	//
	// }

	public void setInterfaces(List<Class> interfaces) {
		this.interfaces = interfaces;
	}

	// public void setOsgiServiceFactoryClass(Class osgiServiceFactoryClass) {
	// this.osgiServiceFactoryClass = osgiServiceFactoryClass;
	// }

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	// public void setUseServiceProviderContextClassLoader(
	// Boolean useServiceProviderContextClassLoader) {
	// this.useServiceProviderContextClassLoader =
	// useServiceProviderContextClassLoader;
	// }

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
}
