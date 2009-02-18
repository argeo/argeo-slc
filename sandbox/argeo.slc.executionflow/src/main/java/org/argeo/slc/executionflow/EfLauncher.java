package org.argeo.slc.executionflow;

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
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionParser;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class EfLauncher {
	private static Log log;

	public static void main(String[] args) {
		init();

		String script = "src/slc/conf/main.xml";
		//describe(script);

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				script);
		// context.start();
		log.info("Context initialized");

		ExecutionFlow main = (ExecutionFlow)context.getBean("main");
		main.execute();
		/*
		Map<String, ExecutionFlow> eFlows = context
				.getBeansOfType(ExecutionFlow.class);
		for (String name : eFlows.keySet()) {
			log.info("##\n## Execute ExecutionFlow " + name);
			ExecutionFlow eFlow = eFlows.get(name);
			eFlow.execute();
			
		}*/
	}
	
	private static void describe(String script){
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);
		reader.loadBeanDefinitions("file:" + script);
		new ConsoleContextDescriber().describeContext(registry);
	}

	private static void init() {
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

}
