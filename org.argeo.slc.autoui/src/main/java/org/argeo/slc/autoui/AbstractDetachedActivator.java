package org.argeo.slc.autoui;

import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.UrlResource;

public class AbstractDetachedActivator implements BundleActivator {
	private final Log log = LogFactory.getLog(getClass());

	private SpringStaticRefProvider staticRefProvider;

	public final void start(BundleContext context) throws Exception {

		Bundle bundle = context.getBundle();

		// Creates application context with this class class loader
		ClassLoader classLoader = getClass().getClassLoader();
		Thread cur = Thread.currentThread();
		ClassLoader save = cur.getContextClassLoader();
		cur.setContextClassLoader(classLoader);

		try {
			AbstractApplicationContext applicationContext = new GenericApplicationContext();
			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(
					(BeanDefinitionRegistry) applicationContext);

			URL url = bundle
					.getResource("META-INF/slc/conf/applicationContext.xml");
			if (url != null) {
				System.out.println("Loads application context from bundle "
						+ bundle.getSymbolicName() + " (url=" + url + ")");
				xmlReader.loadBeanDefinitions(new UrlResource(url));

				// Register static ref provider
				staticRefProvider = new SpringStaticRefProvider(
						applicationContext);
				Properties properties = new Properties();
				properties.setProperty("slc.detached.bundle", bundle
						.getSymbolicName());
				context.registerService(StaticRefProvider.class.getName(),
						staticRefProvider, properties);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not initialize application context");
		} finally {
			cur.setContextClassLoader(save);
		}

		startAutoBundle(context);

		log.info("SLC Detached bundle " + bundle.getSymbolicName() + " ("
				+ bundle.getBundleId() + ") started");
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
