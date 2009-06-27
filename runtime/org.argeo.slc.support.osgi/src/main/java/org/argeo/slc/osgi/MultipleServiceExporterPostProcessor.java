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
import org.springframework.core.PriorityOrdered;
import org.springframework.osgi.service.exporter.support.OsgiServiceFactoryBean;

@SuppressWarnings(value = { "unchecked" })
public class MultipleServiceExporterPostProcessor implements
		BeanFactoryPostProcessor, PriorityOrdered {
	private final static Log log = LogFactory
			.getLog(MultipleServiceExporterPostProcessor.class);

	private List<Class> interfaces = new ArrayList<Class>();

	private Class osgiServiceFactoryClass = OsgiServiceFactoryBean.class;

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof BeanDefinitionRegistry)) {
			throw new SlcException("Can only work on "
					+ BeanDefinitionRegistry.class);
		}

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
			RootBeanDefinition bd = new RootBeanDefinition(
					osgiServiceFactoryClass, mpv);

			String exporterBeanName = "osgiService." + beanName;
			if (log.isDebugEnabled())
				log.debug("Registering OSGi service exporter "
						+ exporterBeanName);
			((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
					exporterBeanName, bd);
		}
	}

	public void setInterfaces(List<Class> interfaces) {
		this.interfaces = interfaces;
	}

	public void setOsgiServiceFactoryClass(Class osgiServiceFactoryClass) {
		this.osgiServiceFactoryClass = osgiServiceFactoryClass;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
