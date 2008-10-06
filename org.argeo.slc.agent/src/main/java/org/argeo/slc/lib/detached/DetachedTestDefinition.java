package org.argeo.slc.lib.detached;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestRun;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.context.ContextUtils;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedStep;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiStep;
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
	private String testDefBeanName = null;
	private BeanDefinitionRegistry beanDefinitionRegistry = null;
	private String stepRef = null;

	public void execute(TestRun testRun) {
		// Look for step bean name
		String stepBeanNameT = null;
		if (this.stepRef == null && step != null) {
			if (step instanceof UiStep) {
				stepBeanNameT = ((UiStep) step).getBeanName();
			} else {
				// Introspects bean factory in order to find step bean name
				BeanDefinition thisBeanDef = beanDefinitionRegistry
						.getBeanDefinition(testDefBeanName);
				PropertyValue propValue = thisBeanDef.getPropertyValues()
						.getPropertyValue("step");
				Object stepBeanRef = propValue.getValue();
				BeanReference ref = (BeanReference) stepBeanRef;
				stepBeanNameT = ref.getBeanName();
			}
		} else if (this.stepRef != null) {
			stepBeanNameT = this.stepRef;
		}

		// Execute
		DetachedRequest request = new DetachedRequest();
		request.setPath(getBasePath().toString());
		request.setUuid(UUID.randomUUID().toString());
		request.setRef(stepBeanNameT);

		DetachedTestData testData = testRun.getTestData();
		if (testData != null) {
			Map<String, Object> values = testData.getValues();
			Properties inputParameters = new Properties();
			inputParameters.putAll(values);// TODO: check conversions to string
			request.setProperties(inputParameters);
		}

		try {
			client.sendRequest(request);
		} catch (Exception e) {
			throw new SlcException("Could not send request for step "
					+ stepBeanNameT, e);
		}

		try {
			DetachedAnswer answer = client.receiveAnswer();
			if (answer.getStatus() == DetachedAnswer.ERROR)
				throw new SlcException("Error when executing step "
						+ answer.getUuid() + ": " + answer.getLog());
			else
				log.info("Received answer for '" + request.getRef() + "' ("
						+ answer.getStatusAsString() + "):" + answer.getLog());

			if (testData != null) {
				Properties outputParameters = answer.getProperties();
				for (Object key : outputParameters.keySet())
					testData.getValues().put(key.toString(),
							outputParameters.get(key));
			}
		} catch (Exception e) {
			throw new SlcException("Could not receive answer #"
					+ request.getUuid() + " for step " + stepBeanNameT, e);
		}

		if (testData != null) {
			ContextUtils.compareReachedExpected(testData, testRun
					.getTestResult(), this);
		} else {
			testRun.getTestResult().addResultPart(
					new SimpleResultPart(TestStatus.PASSED, "Step "
							+ stepBeanNameT + " executed successfully"));
		}
	}

	public void setBeanName(String name) {
		this.testDefBeanName = name;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof BeanDefinitionRegistry)
			beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
		else
			throw new BeanInitializationException(
					"Require BeanDefinitionRegistry");
	}

	public void afterPropertiesSet() throws Exception {
	}

	public void setStep(DetachedStep step) {
		this.step = step;
	}

	public void setClient(DetachedClient client) {
		this.client = client;
	}

	public void setStepRef(String stepBeanName) {
		this.stepRef = stepBeanName;
	}

}
