package org.argeo.slc.jemmytest;

import java.util.Properties;

import org.argeo.slc.autoui.AutoUiApplication;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JemmyTestActivator implements BundleActivator {
	private AbstractApplicationContext applicationContext;

	public void start(BundleContext context) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();

		Thread cur = Thread.currentThread();
		ClassLoader save = cur.getContextClassLoader();
		cur.setContextClassLoader(classLoader);

		try {
			// FIXME: make it more generic
			applicationContext = new ClassPathXmlApplicationContext(
					"/slc/conf/applicationContext.xml");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not initialize application context");
		} finally {
			cur.setContextClassLoader(save);
		}

		// applicationContext = new GenericApplicationContext();
		// XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(
		// (BeanDefinitionRegistry) applicationContext);
		// Bundle bundle = context.getBundle();
		//
		// URL url = bundle
		// .getResource("META-INF/slc/conf/applicationContext.xml");
		// if (url != null) {
		// System.out.println("Loads application context from bundle "
		// + bundle.getSymbolicName());
		// xmlReader.loadBeanDefinitions(new UrlResource(url));
		// }

		Properties properties = new Properties();
		// AutoUiApplicationJemmy applicationJemmy = new
		// AutoUiApplicationJemmy();
		AutoUiApplicationJemmy applicationJemmy = (AutoUiApplicationJemmy) applicationContext
				.getBean("jemmyTest");
		context.registerService(AutoUiApplication.class.getName(),
				applicationJemmy, properties);
		stdOut("JemmyTest started");
	}

	public void stop(BundleContext context) throws Exception {
		stdOut("JemmyTest stopped");
	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}

}
