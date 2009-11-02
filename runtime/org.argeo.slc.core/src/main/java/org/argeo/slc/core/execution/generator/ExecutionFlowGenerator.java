package org.argeo.slc.core.execution.generator;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * Generates <code>ExecutionFlows</code> and <code>Runnables</code> as
 * beans in the Spring Application Context.
 * Called by the Application Context as a <code>BeanFactoryPostProcessor</code>.
 * Two kinds of beans are generated:
 * <code>RunnableCallFlow</code>, calling a list of <code>Runnables</code> from the
 * Application Context after configuring the <code>ExecutionContext</code>, 
 * and outputs of a <code>RunnableFactory</code>.
 */
public class ExecutionFlowGenerator implements BeanFactoryPostProcessor,
		PriorityOrdered {
	
	private final Log log = LogFactory.getLog(getClass());

	/**
	 * Source providing a list of <code>RunnableCallFlowDescriptor</code> 
	 * used to create <code>RunnableCallFlow</code> and a list of 
	 * <code>RunnableDataNode</code> used to create any kind of flow via a factory
	 */
	protected ExecutionFlowGeneratorSource source;
	
	/**
	 * Factory used to create Runnables in the Application context from
	 * the <code>RunnableDataNode</code> provided from the source.
	 */
	protected RunnableFactory runnableFactory;
	
	/**
	 * Bean name of the <code>ExecutionContext</code>.
	 * Used to provide the created <code>RunnableCallFlow</code> beans 
	 * with a <code>RuntimeBeanReference</code> to
	 * the <code>ExecutionContext</code>
	 */
	private String executionContextBeanName = "executionContext";
	
	/**
	 * Bean name of the context values Map.
	 * A bean of class HashMap is created with this name, and a 
	 * <code>RuntimeBeanReference</code> is provided to the created
	 * <code>RunnableCallFlow</code> beans.
	 */
	private String contextValuesBeanName = "executionFlowGenerator.contextValues";
	
	/**
	 * Prefix added to the bean names defined in each 
	 * <code>RunnableCallFlowDescriptor</code>
	 */
	private String flowBeanNamesPrefix = "";
		
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {

		// assert that the beanFactory is a BeanDefinitionRegistry
		if (!(beanFactory instanceof BeanDefinitionRegistry)) {
			throw new SlcException("Can only work on "
					+ BeanDefinitionRegistry.class);
		} 
		
		// add bean for the Context Values Map
		createAndRegisterContextValuesBean((BeanDefinitionRegistry) beanFactory);
		
		// add beans for each RunnableDataNode
		for(RunnableDataNode node : source.getRunnableDataNodes()) {
			runnableFactory.createAndRegisterRunnable(node, (BeanDefinitionRegistry) beanFactory);
		}
		
		// add beans for each RunnableCallFlowDescriptor of the source to the application context
		for (RunnableCallFlowDescriptor descriptor : source
				.getRunnableCallFlowDescriptors()) {
			createAndRegisterFlowFor(descriptor, (BeanDefinitionRegistry) beanFactory);
		}
	}

	/**
	 * Creates a <code>RunnableCallFlow</code> bean
	 * for a <code>RunnableCallFlowDescriptor</code> and registers 
	 * it in the <code>BeanDefinitionRegistry</code>
	 * @param flowDescriptor
	 * @param registry
	 */
	private void createAndRegisterFlowFor(RunnableCallFlowDescriptor flowDescriptor, BeanDefinitionRegistry registry) {
		// create the flow bean
		GenericBeanDefinition flowBean = new GenericBeanDefinition();
		flowBean.setBeanClass(RunnableCallFlow.class);
		
		String beanName = flowBeanNamesPrefix + flowDescriptor.getBeanName();
		
		MutablePropertyValues mpv = new MutablePropertyValues();		
		mpv.addPropertyValue("runnableCalls", flowDescriptor.getRunnableCalls());
		mpv.addPropertyValue("sharedContextValuesMap", new RuntimeBeanReference(contextValuesBeanName));
		
		mpv.addPropertyValue("name", beanName);
		mpv.addPropertyValue("path", flowDescriptor.getPath());

		mpv.addPropertyValue("executionContext", new RuntimeBeanReference(executionContextBeanName));
		
		flowBean.setPropertyValues(mpv);
		
		// register it
		if(log.isDebugEnabled()) {
			log.debug("Registering bean definition for RunnableCallFlow " + beanName);
		}
		registry.registerBeanDefinition(beanName, flowBean);
	}
	
	/**
	 * Creates the Context Values bean and register it in the
	 * <code>BeanDefinitionRegistry</code>
	 * @param registry
	 */
	private void createAndRegisterContextValuesBean(BeanDefinitionRegistry registry) {
		GenericBeanDefinition contextValuesBean = new GenericBeanDefinition();
		contextValuesBean.setBeanClass(HashMap.class);
		
		BeanDefinitionHolder bdh = ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(contextValuesBean, contextValuesBeanName), registry, true);															
		registry.registerBeanDefinition(contextValuesBeanName, bdh.getBeanDefinition());		
	}
	
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	public void setSource(ExecutionFlowGeneratorSource source) {
		this.source = source;
	}

	public void setRunnableFactory(RunnableFactory runnableFactory) {
		this.runnableFactory = runnableFactory;
	}

	public void setExecutionContextBeanName(String executionContextBeanName) {
		this.executionContextBeanName = executionContextBeanName;
	}

	public void setContextValuesBeanName(String contextValuesBeanName) {
		this.contextValuesBeanName = contextValuesBeanName;
	}

	public void setFlowBeanNamesPrefix(String flowBeanNamesPrefix) {
		this.flowBeanNamesPrefix = flowBeanNamesPrefix;
	}
}
