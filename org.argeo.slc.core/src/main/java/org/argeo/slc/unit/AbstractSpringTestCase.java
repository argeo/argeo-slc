package org.argeo.slc.unit;

import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;

/** Helper for tests using a Spring application co,text. */
public abstract class AbstractSpringTestCase extends TestCase {
	private ApplicationContext context;

	/**
	 * Gets (and create if necessary) the application context to use. Default
	 * implementation uses a class path xml application context and calls
	 * {@link #getApplicationContextLocation()}.
	 */
	protected ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext(
					getApplicationContextLocation());
		}
		return context;
	}

	/** Returns a bean from the underlying context */
	protected <T> T getBean(String beanId) {
		return (T) getContext().getBean(beanId);
	}

	protected <T> T getBean(Class<? extends T> clss) {
		Map<String, T> map = getContext().getBeansOfType(clss);
		if (map.size() == 1) {
			return map.values().iterator().next();
		} else {
			throw new SlcException("Cannot retrieve a unique bean of type "
					+ clss);
		}
	}

	/**
	 * Th location of the application to load. The default implementation
	 * returns <i>applicationContext.xml</i> found in the same package as the
	 * test.
	 */
	protected String getApplicationContextLocation() {
		return inPackage("applicationContext.xml");
	}

	/**
	 * Prefixes the package of the class after converting the '.' to '/' in
	 * order to have a resource path.
	 */
	protected String inPackage(String suffix) {
		String prefix = getClass().getPackage().getName().replace('.', '/');
		return prefix + '/' + suffix;
	}
}
