package org.argeo.slc.example;

import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.cli.DefaultSlcRuntime;
import org.argeo.slc.detached.SpringStaticRefProvider;
import org.argeo.slc.detached.StaticRefProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.UrlResource;

public class Activator implements BundleActivator {
	//private final static String BOOTSTRAP_LOG4J_CONFIG = "org/argeo/slc/example/log4j.properties";
	private static Log log = LogFactory.getLog(Activator.class);

	public void start(BundleContext context) throws Exception {
		initLogging(null);
		System.out.println("(stdout) Starting SLC Example bundle XXX...");
		log.info("Starting SLC Example bundle...");
		
		
		// Creates application context with this class class loader
		ClassLoader classLoader = getClass().getClassLoader();
		Thread cur = Thread.currentThread();
		ClassLoader save = cur.getContextClassLoader();
		cur.setContextClassLoader(classLoader);

		try {
			DefaultSlcRuntime runtime = new DefaultSlcRuntime();
			runtime
					.executeScript(
							null,
							"/home/mbaudier/dev/src/slc/org.argeo.slc.example/src/main/slc/root/Category1/SubCategory2/build.xml",
							null, new Properties(), null, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not initialize application context");
		} finally {
			cur.setContextClassLoader(save);
		}


	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("(stdout) Stop SLC Example bundle...");
	}

	private static void initLogging(Properties userProperties) {
		//System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.rootLogger", "WARN, console");
		System.setProperty("log4j.logger.org.argeo", "INFO");
		System.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		System.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
		System.setProperty("log4j.appender.console.layout.ConversionPattern", "%-5p %d{ISO8601} %m - %c%n");

//		// Add log4j user properties to System properties
//		for (Object obj : userProperties.keySet()) {
//			String key = obj.toString();
//			if (key.startsWith("log4j.")) {
//				System.setProperty(key, userProperties.getProperty(key));
//			}
//		}
//		Log4jUtils.initLog4j(System.getProperty("log4j.configuration",
//				"classpath:" + BOOTSTRAP_LOG4J_CONFIG));
		log = LogFactory.getLog(Activator.class);

	}

}
