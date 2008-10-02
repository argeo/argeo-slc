package org.argeo.slc.lib.detached;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestRun;
import org.argeo.slc.core.test.context.ContextUtils;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedStep;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class DetachedTestDefinition extends TreeSRelatedHelper implements
		TestDefinition, BeanNameAware, BeanFactoryAware, InitializingBean {
	private final static Log log = LogFactory
			.getLog(DetachedTestDefinition.class);

	private DetachedStep step;
	private DetachedClient client;

	// Spring properties
	private String thisBeanName = null;
	private BeanDefinitionRegistry beanDefinitionRegistry = null;
	private String stepBeanName = null;

	public void execute(TestRun testRun) {
		DetachedTestData testData = testRun.getTestData();
		Map<String, Object> values = testData.getValues();
		Properties inputParameters = new Properties();
		inputParameters.putAll(values);// TODO: check conversions to string

		DetachedRequest request = new DetachedRequest();
		request.setPath(getBasePath().toString());
		request.setUuid(UUID.randomUUID().toString());
		request.setRef(stepBeanName);
		request.setProperties(inputParameters);

		try {
			client.sendRequest(request);
			log.debug("Sent detached request #" + request.getUuid()
					+ " for step " + stepBeanName);
		} catch (Exception e) {
			throw new SlcException("Could not send request for step "
					+ stepBeanName, e);
		}

		try {
			DetachedAnswer answer = client.receiveAnswer();
			Properties outputParameters = answer.getProperties();
			for (Object key : outputParameters.keySet())
				testData.getValues().put(key.toString(),
						outputParameters.get(key));
			log.debug("Received detached answer #" + answer.getUuid()
					+ " for step " + stepBeanName);
		} catch (Exception e) {
			throw new SlcException("Could not receive answer #"
					+ request.getUuid() + " for step " + stepBeanName, e);
		}

		ContextUtils.compareReachedExpected(testData, testRun.getTestResult(),
				this);
	}

	public void setBeanName(String name) {
		this.thisBeanName = name;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof BeanDefinitionRegistry)
			beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
		else
			throw new BeanInitializationException(
					"Require BeanDefinitionRegistry");
	}

	public void afterPropertiesSet() throws Exception {
		if (stepBeanName == null) {
			// Introspects bean factory in order to find step bean name
			BeanDefinition thisBeanDef = beanDefinitionRegistry
					.getBeanDefinition(thisBeanName);
			PropertyValue propValue = thisBeanDef.getPropertyValues()
					.getPropertyValue("step");
			Object stepBeanRef = propValue.getValue();
			log.info("stepBeanRef.class=" + stepBeanRef.getClass());
			BeanReference ref = (BeanReference) stepBeanRef;
			stepBeanName = ref.getBeanName();
		}
	}

	public void setStep(DetachedStep step) {
		this.step = step;
	}

	public void setClient(DetachedClient client) {
		this.client = client;
	}

	public void setStepBeanName(String stepBeanName) {
		this.stepBeanName = stepBeanName;
	}

}
