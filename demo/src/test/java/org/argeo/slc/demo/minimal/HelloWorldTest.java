package org.argeo.slc.demo.minimal;

import junit.framework.TestCase;

import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class HelloWorldTest extends TestCase {
	public void testHelloWorld() throws Exception {
		GenericBeanFactoryAccessor context = new GenericBeanFactoryAccessor(
				createContext());
		ExecutionFlow flow = context.getBean("main");
		flow.run();
	}

	protected ConfigurableApplicationContext createContext() {
		String[] locations = { "site/org.argeo.slc.demo.groovy/slc/main.xml",
				"site/org.argeo.slc.demo.groovy/slc/imports.xml" };
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				locations);
		return context;
	}

}
