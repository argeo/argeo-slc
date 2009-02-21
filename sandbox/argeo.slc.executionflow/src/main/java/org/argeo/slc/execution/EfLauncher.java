package org.argeo.slc.execution;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.logging.Log4jUtils;
import org.argeo.slc.process.SlcExecution;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionParser;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class EfLauncher implements ApplicationListener {
	private final Log log;

	private boolean running = false;

	public EfLauncher() {
		Properties userProperties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream("src/slc/conf/slc.properties");
			userProperties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}

		// Set as System properties
		for (Object obj : userProperties.keySet()) {
			String key = obj.toString();
			System.setProperty(key, userProperties.getProperty(key));
		}

		// Logging
		System.setProperty("log4j.defaultInitOverride", "true");

		Log4jUtils.initLog4j(null);
		log = LogFactory.getLog(EfLauncher.class);
	}

	public void launch(String script) {
		// describe(script);

		GenericApplicationContext context = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		reader.loadBeanDefinitions(script);
		// FileSystemXmlApplicationContext context = new
		// FileSystemXmlApplicationContext(
		// script);
		context.addApplicationListener(this);
		context.refresh();
		context.start();
		log.debug("Context initialized");

		SlcExecution slcExecution = new SlcExecution();
		slcExecution.getAttributes().put("slc.flows", "main");

		running = true;
		context.publishEvent(new NewExecutionEvent(this, slcExecution));

		synchronized (this) {
			while (running)
				try {
					wait();
				} catch (InterruptedException e) {
					// silent
				}
		}
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ExecutionFinishedEvent) {
			ExecutionContext executionContext = ((ExecutionFinishedEvent) event)
					.getExecutionContext();
			log.debug("Execution " + executionContext.getUuid()
					+ " finished, stopping launcher...");
			synchronized (this) {
				running = false;
				notifyAll();
			}
		}

	}

	public static void main(String[] args) {
		String script = "file:src/slc/conf/main.xml";
		new EfLauncher().launch(script);
	}

	private static void describe(String script) {
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);
		reader.loadBeanDefinitions(script);
		new ConsoleContextDescriber().describeContext(registry);
	}
}
