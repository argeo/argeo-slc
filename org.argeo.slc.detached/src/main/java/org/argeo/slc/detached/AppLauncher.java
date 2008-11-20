package org.argeo.slc.detached;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class AppLauncher {
	private Properties systemProperties = new Properties();
	private String mainClass = null;
	private List arguments = new Vector();

	public void launch() {
		try {
			if (mainClass == null)
				throw new DetachedException(
						"A main class name muste be specified.");

			System.getProperties().putAll(systemProperties);
			//Class clss = getClass().getClassLoader().loadClass(mainClass);
			Class clss = Class.forName(mainClass);

			String[] args = new String[arguments.size()];
			for (int i = 0; i < arguments.size(); i++) {
				args[i] = arguments.get(i).toString();
			}

			Class[] mainArgsClasses = new Class[] { args.getClass() };
			Object[] mainArgs = { args };
			Method mainMethod = clss.getMethod("main", mainArgsClasses);
			mainMethod.invoke(null, mainArgs);

		} catch (Exception e) {
			throw new DetachedException("Unexpected exception while launching "
					+ mainClass, e);
		}

	}

	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public void setArguments(List arguments) {
		this.arguments = arguments;
	}

}
