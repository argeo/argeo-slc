package org.argeo.slc.autoui;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.UrlResource;

public class AbstractDetachedActivator implements BundleActivator {
	private AbstractApplicationContext applicationContext;

	public final void start(BundleContext context) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();

		Thread cur = Thread.currentThread();
		ClassLoader save = cur.getContextClassLoader();
		cur.setContextClassLoader(classLoader);

		try {
			// applicationContext = new ClassPathXmlApplicationContext(
			// "/slc/conf/applicationContext.xml");

			applicationContext = new GenericApplicationContext();
			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(
					(BeanDefinitionRegistry) applicationContext);
			Bundle bundle = context.getBundle();

			URL url = bundle
					.getResource("META-INF/slc/conf/applicationContext.xml");
			if (url != null) {
				System.out.println("Loads application context from bundle "
						+ bundle.getSymbolicName() + " (url=" + url + ")");
				xmlReader.loadBeanDefinitions(new UrlResource(url));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not initialize application context");
		} finally {
			cur.setContextClassLoader(save);
		}

		startAutoBundle(context);
	}

	/** Does nothing by default. */
	protected void startAutoBundle(BundleContext context) throws Exception {

	}

	public final void stop(BundleContext context) throws Exception {
		stopAutoBundle(context);

		if (applicationContext != null) {
			applicationContext.close();
		}

	}

	/** Does nothing by default. */
	protected void stopAutoBundle(BundleContext context) throws Exception {

	}

	public Object getStaticRef(String id) {
		return applicationContext.getBean(id);
	}

}
