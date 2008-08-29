package org.argeo.slc.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.argeo.slc.core.SlcException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

public class Log4jUtils {

	/**
	 * Configure log4j based on properties, with the following priorities (from
	 * highest to lowest):<br>
	 * 1. System properties<br>
	 * 2. configuration file itself
	 */
	public static void initLog4j(String configuration) {
		// clears previous configuration
		shutDownLog4j();

		ClassLoader cl = Log4jUtils.class.getClassLoader();
		Properties properties = new Properties();
		if (configuration != null) {
			InputStream in = null;
			try {
				if (configuration
						.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
					String path = configuration
							.substring(ResourceUtils.CLASSPATH_URL_PREFIX
									.length());
					in = cl.getResourceAsStream(path);
				} else {
					in = new DefaultResourceLoader(cl).getResource(
							configuration).getInputStream();
				}

				properties.load(in);
			} catch (IOException e) {
				throw new SlcException("Cannot load properties from "
						+ configuration);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}

		// Overrides with System properties
		overrideLog4jProperties(properties, System.getProperties());

		PropertyConfigurator.configure(properties);
	}

	private static void overrideLog4jProperties(Properties target,
			Properties additional) {
		for (Object obj : additional.keySet()) {
			String key = obj.toString();
			if (key.startsWith("log4j.")) {
				if (!key.equals("log4j.configuration")) {
					String value = SystemPropertyUtils
							.resolvePlaceholders(additional.getProperty(key));
					target.put(key, value);
				}
			}
		}
	}

	public static void shutDownLog4j() {
		LogManager.shutdown();
	}

	private Log4jUtils() {

	}
}
