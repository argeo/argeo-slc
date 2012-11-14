/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.detached;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppLauncher {
	private Properties systemProperties = new Properties();
	private String mainClass = null;
	private List arguments = new ArrayList();

	public void launch() {

		Properties base = System.getProperties();
		Properties fake = new Properties(base);

		try {
			if (mainClass == null)
				throw new DetachedException(
						"A main class name has to be specified.");

			System.getProperties().putAll(systemProperties);

			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Class clss = cl.loadClass(mainClass);

			String[] args = new String[arguments.size()];
			for (int i = 0; i < arguments.size(); i++) {
				args[i] = arguments.get(i).toString();
			}

			Class[] mainArgsClasses = new Class[] { args.getClass() };
			Object[] mainArgs = { args };
			Method mainMethod = clss.getMethod("main", mainArgsClasses);

			System.setProperties(fake);

			mainMethod.invoke(null, mainArgs);

		} catch (Exception e) {
			throw new DetachedException("Unexpected exception while launching "
					+ mainClass, e);
		} finally {
			System.setProperties(base);
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
