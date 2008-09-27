package org.argeo.slc.autoui;

import java.net.URL;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.UrlResource;

public class AbstractDetachedActivator implements BundleActivator {
	private SpringStaticRefProvider staticRefProvider;

	public final void start(BundleContext context) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();

		// Creates application context
		Thread cur = Thread.currentThread();
		ClassLoader save = cur.getContextClassLoader();
		cur.setContextClassLoader(classLoader);

		try {
			// applicationContext = new ClassPathXmlApplicationContext(
			// "/slc/conf/applicationContext.xml");

			AbstractApplicationContext applicationContext = new GenericApplicationContext();
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

			// Register static ref provider
			staticRefProvider = new SpringStaticRefProvider(applicationContext);
			Properties properties = new Properties();
			properties.setProperty("slc.detached.bundle", bundle
					.getSymbolicName());
			context.registerService(StaticRefProvider.class.getName(),
					staticRefProvider, properties);

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

		if (staticRefProvider != null) {
			staticRefProvider.close();
		}

	}

	/** Does nothing by default. */
	protected void stopAutoBundle(BundleContext context) throws Exception {

	}

	protected StaticRefProvider getStaticRefProvider() {
		return staticRefProvider;
	}
}
