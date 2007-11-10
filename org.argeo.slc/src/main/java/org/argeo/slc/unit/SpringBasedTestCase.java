package org.argeo.slc.unit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class SpringBasedTestCase extends TestCase {
	private ApplicationContext context;

	/**
	 * Gets (and create if necessary) the application context to use. Default
	 * implementation uses a class path xml application context and calls
	 * {@link #getApplicationContextLocation()}.
	 */
	protected ApplicationContext getApplicationContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext(
					getApplicationContextLocation());
		}
		return context;
	}

	/**
	 * Get the application context location used by the default implementation
	 * of {@link #getApplicationContext()}.
	 */
	protected String getApplicationContextLocation() {
		return inPackage("applicationContext.xml");
	}
	
	protected String inPackage(String suffix){
		String prefix = getClass().getPackage().getName().replace('.', '/');
		return prefix+'/'+suffix;
	}
}
